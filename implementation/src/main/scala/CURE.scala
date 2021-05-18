import Utils.{euclideanDistance, euclideanDistanceP}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import Numeric.Implicits._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer



object CURE {
  case class Response (clusters:List[CureCluster], outliers:List[Point])
  case class ResponseRDD (points:RDD[(Point,Long)],outliers:RDD[Point])

  def initializeClusters(points: List[Point]): List[CureCluster] = {
    val spark = SparkSession.builder.getOrCreate()
    val indexed = points.zipWithIndex
    val bd = spark.sparkContext.broadcast(indexed)
    indexed.map(p => {
      val points = List[Point](p._1)
      val c_id = p._2
      val closest: (Int, Double) = bd.value
        .filter(p2 => {
          p2._2 != p._2
        })
        .map(p2 => {
          (p2._2, euclideanDistanceP(p._1, p2._1))
        }).minBy(_._2)
      new CureCluster(c_id, points, points, p._1.values, closest._1, closest._2)
    })
  }

  def merge(u: CureCluster, v: CureCluster, c: Int, a: Double): CureCluster = {
    val newPoints: List[Point] = u.points ++ v.points
    val newMean = calculateMean(u.mean, u.points.size, v.mean, v.points.size)
    val tmpSet: mutable.HashSet[Point] = new mutable.HashSet[Point]
    val newRepresentatives: ListBuffer[Point] = new ListBuffer[Point]()
    if (c < newPoints.size) {
      for (i <- 1 to c) {
        var maxDist: Double = 0
        var maxPoint: Point = new Point("0,0")
        newPoints.foreach(point => {
          var minDist = Double.MaxValue
          if (i == 1) {
            minDist = Utils.euclideanDistance(point.values, newMean)
          } else {
            minDist = tmpSet.map(pSet => {
              Utils.euclideanDistanceP(pSet, point)
            }).min
          }
          if (minDist >= maxDist) {
            maxDist = minDist
            maxPoint = point
          }
        })
        tmpSet.add(maxPoint)
      }
      tmpSet.foreach(point => {
        val newCoordinates = point.values.zip(newMean)
          .map(pair => {
            pair._1 + a * pair._2
          })
        val p: Point = new Point(newCoordinates.mkString(","))
        newRepresentatives += p
      })
    } else {
      newRepresentatives.appendAll(newPoints)
    }
    new CureCluster(u.c_id, newRepresentatives.toList, newPoints, newMean, -1, -1)

  }

  def parallelCure(points:RDD[Point],k: Int, c: Int, a: Double): Response ={
    val all_clusters = points.mapPartitions(partition=>{
      val clusters = this.initializeClusters(partition.toList)
      val cure = this.cure_algorithm(clusters,k,c,a)
      cure.clusters.toIterator
    })
    cure_algorithm(all_clusters.collect().toList,k,c,a)
  }

  def cure_algorithm(clusters: List[CureCluster], k: Int, c: Int, a: Double): Response = {
    val minHeap = new MinHeap(clusters)
    val outlierPoints:ListBuffer[Point] = new ListBuffer[Point]();
    minHeap.build_heap()
    val check_for_outliers = Math.floor(clusters.size/3).toInt
    while (minHeap.size() > k) {
      if(minHeap.size()==check_for_outliers){
        remove_outliers(minHeap,1).foreach(op=>{ //remove all clusters with size less or equal to 2
          outlierPoints+=op
        })
      }
      val u = minHeap.extract_min()
      val v = minHeap.get(u.closest)
      minHeap.delete(v.get.c_id)
      val w = merge(u,v.get,c,a) // the closest cluster or the distance hasn't been determined yet
      for(x:CureCluster <- minHeap.getIterable().toList){
        if(w.closest == -1) {
          w.closest = x.c_id
          w.distance = Utils.distanceClusters(w,x)
        }else if(Utils.distanceClusters(w,x)<Utils.distanceClusters(w,minHeap.get(w.closest).get)){
          w.closest = x.c_id
          w.distance= Utils.distanceClusters(w,x)
        }
        if(x.closest==u.c_id || x.closest== v.get.c_id){
          if(x.distance<Utils.distanceClusters(w,x)){
            val closest = this.findClosestCluster(x,minHeap)
            x.closest = closest.c_id
            x.distance = Utils.distanceClusters(x,closest)
          }else{
            x.closest=w.c_id
            x.distance=Utils.distanceClusters(x,w)
          }
          minHeap.relocate(x)
        }else if(Utils.distanceClusters(x,minHeap.get(x.closest).get)>Utils.distanceClusters(x,w)){
          x.closest=w.c_id
          x.distance=Utils.distanceClusters(x,w)
          minHeap.relocate(x)
        }
        minHeap.relocate(x)
      }
      minHeap.insert(w)
    }
    Response(minHeap.getIterable().toList,outlierPoints.toList)
  }

  /**
   * Assign each point to the closest representative
   * Report as outleirs the points that deciates more than n*stdev from the mean value of all the distances
   * @param points
   * @param clusters
   * @param n
   * @return
   */
  def pass_data(points:RDD[Point],clusters:List[CureCluster],n:Double): ResponseRDD ={
    val representatives=clusters.flatMap(c=>{
      c.repr.map(r=>{
        (r,c.c_id)
      })
    })
    val sc = SparkSession.builder().getOrCreate()
    val b_repr = sc.sparkContext.broadcast(representatives)
    val mapping = points.map(p=>{
      val c = b_repr.value.map(r=>{
        (Utils.euclideanDistanceP(p,r._1),r._2)
      })
        .minBy(_._1)
      (p,c._2,c._1)
    })
    val distances = mapping.map(_._3).collect()
    val m = mean(distances)
    val std = stdDev(distances)
    val threshold = m + n*std
    val outliersRDD = mapping.filter(_._3>=threshold).map(_._1)
    val normalRDD = mapping.filter(_._3<threshold).map(x=>(x._1,x._2))
    ResponseRDD(normalRDD,outliersRDD)

  }

  private def findClosestCluster(x:CureCluster,minHeap: MinHeap): CureCluster ={
    val others = minHeap.getIterable()
      .filter(_.c_id!=x.c_id)
      .map(c=>{
        (c.c_id,Utils.distanceClusters(c,x),c)
      })
      if (others.nonEmpty){
        others.minBy(_._2)
          ._3
      }else{
        null
      }

  }

  private def calculateMean(m1: List[Double], s1: Int, m2: List[Double], s2: Int): List[Double] = {
    m1.zip(m2).map(pair => {
      (s1 * pair._1 + s2 * pair._2) / (s1 + s2)
    })
  }

  private def remove_outliers(minHeap:MinHeap,threshold:Int): List[Point] ={
    val outlier_clusters:List[CureCluster] = minHeap.getIterable().filter(_.points.size<=threshold).toList
    val ids = outlier_clusters.map(_.c_id)
    ids.foreach(id=>{
      minHeap.delete(id)
    })
    minHeap.getIterable().foreach(c=>{
      if(ids.contains(c.closest)){
        val closest = this.findClosestCluster(c,minHeap)
        c.closest = closest.c_id
        c.distance=Utils.distanceClusters(c,closest)
        minHeap.relocate(c)
      }
    })

    outlier_clusters.flatMap(_.points)


  }

  private def mean[T: Numeric](xs: Iterable[T]): Double = xs.sum.toDouble / xs.size

  private def variance[T: Numeric](xs: Iterable[T]): Double = {
    val avg = mean(xs)

    xs.map(_.toDouble).map(a => math.pow(a - avg, 2)).sum / xs.size
  }

  private def stdDev[T: Numeric](xs: Iterable[T]): Double = math.sqrt(variance(xs))




}
