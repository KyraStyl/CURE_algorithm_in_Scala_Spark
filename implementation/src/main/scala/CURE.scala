import Utils.euclideanDistance
import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object CURE {

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
          (p2._2, euclideanDistance(p._1, p2._1))
        }).minBy(_._2)
      new CureCluster(c_id, points, points, p._1.values, closest._1, closest._2)
    })
  }

  def merge(u: CureCluster, v: CureCluster, c: Int, a: Double): CureCluster = {
    val newPoints: List[Point] = u.points ++ v.points
    val newMean = calculateMean(u.mean, u.points.size, v.mean, v.points.size)
    val tmpSet: mutable.HashSet[Point] = new mutable.HashSet[Point]
    val newRepresentatives: ListBuffer[Point] = new ListBuffer[Point]()
    if (c > newPoints.size) {
      for (i <- 1 to c) {
        println(i)
        var maxDist: Double = 0
        var maxPoint: Point = new Point("0,0")
        newPoints.foreach(point => {
          var minDist = Double.MaxValue
          if (i == 1) {
            minDist = Utils.euclideanDistance(point.values, newMean)
          } else {
            minDist = tmpSet.map(pSet => {
              Utils.euclideanDistance(pSet, point)
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
        val p: Point = new Point("")
        p.values = newCoordinates
        newRepresentatives += p
      })
    } else {
      newRepresentatives.appendAll(newPoints)
    }
    new CureCluster(u.c_id, newRepresentatives.toList, newPoints, newMean, -1, -1)

  }


  def cure_algorithm(points: List[Point], k: Int, c: Int, a: Double, threshold: Double): List[CureCluster] = {
    val clusters = this.initializeClusters(points)
    val minHeap = new MinHeap(clusters)
    minHeap.build_heap()
    while (minHeap.size() > k) {
      val u = minHeap.extract_min()
      val v = minHeap.get(u.closest)
      minHeap.delete(v.c_id)
      val w = merge(u,v,c,a) // the closest cluster or the distance hasn't been determined yet
      minHeap.getIterable().foreach(x=>{
        if(w.closest == -1 || Utils.distanceClusters(w,x)<Utils.distanceClusters(w,minHeap.get(w.closest))) {
          w.closest = x.c_id
        }
        if(x.closest==u.c_id || x.closest== v.c_id){
          if(Utils.distanceClusters(x,minHeap.get(x.closest))<Utils.distanceClusters(w,x)){
            x.closest = this.findClosestCluster(x,minHeap)
          }else{
            x.closest=w.c_id
          }
          minHeap.relocate(x)
        }else if(Utils.distanceClusters(x,minHeap.get(x.closest))>Utils.distanceClusters(x,w)){
          x.closest=w.c_id
          minHeap.relocate(x)
        }
        minHeap.insert(x)
      })
    }
    minHeap.getIterable().toList


  }

  private def findClosestCluster(x:CureCluster,minHeap: MinHeap): Long ={
    minHeap.getIterable()
      .filter(_.c_id!=x.c_id)
      .map(c=>{
        (c.c_id,Utils.distanceClusters(c,x))
      })
      .minBy(_._2)
      ._1
  }

  private def calculateMean(m1: List[Double], s1: Int, m2: List[Double], s2: Int): List[Double] = {
    m1.zip(m2).map(pair => {
      (s1 * pair._1 + s2 * pair._2) / (s1 + s2)
    })
  }


}
