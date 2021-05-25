import org.apache.spark.rdd.RDD

import java.io.{File, PrintWriter}

object Utils {

  def euclideanDistanceP(p1: Point, p2: Point): Double = {
    this.euclideanDistance(p1.values, p2.values)
  }

  def euclideanDistance(p1: List[Double], p2: List[Double]): Double = {
    Math.sqrt(p1.zip(p2).map(pair => {
      Math.pow(pair._1 - pair._2, 2)
    }).sum)
  }

  def distanceClusters(c1: CureCluster, c2: CureCluster): Double = {
    c1.repr.flatMap(p1 => {
      c2.repr.map(p2 => {
        this.euclideanDistanceP(p1, p2)
      })
    }).min
  }

  def writePoints(data: RDD[(Point, Long)], dir: String): Unit = {

    //using saveAsTextFile
    //data.map(x => x._1.toString+","+x._2.toString).coalesce(1).saveAsTextFile("test")

    val outfile = new File(dir+"/points.txt")
    val pw = new PrintWriter(outfile)

    for(x<-data.collect()){
    pw.write(x._1.toString+","+x._2.toString)
    pw.write("\n")
    }
    pw.close()
  }


   def writeOutliers(data:RDD[Point], dir: String): Unit ={
    val outfile = new File(dir+"/outliers.txt")
    val pw = new PrintWriter(outfile)

    for(x<-data.collect()){
      pw.write(x.toString)
      pw.write("\n")
    }
    pw.close()
  }

  def writeToFile(data: CURE.ResponseRDD,dir:String): Unit ={
    val directory = new File(dir)
    if (! directory.exists()) {
      directory.mkdirs()
    }
    writePoints(data.points,dir)
    writeOutliers(data.outliers,dir)
  }

}


