import org.apache.log4j.{Level, Logger}

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf

object Main extends App{

  // Create spark configuration
  val sparkConf = new SparkConf()
    .setAppName("CureAlgorithm")
  val spark = SparkSession.builder().config(sparkConf).getOrCreate()
  val sc = spark.sparkContext

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  if (args.length != 6) {
    println("The programm takes 6 parameters. \n\t1.Input dataset txt\n\t2.Number of clusters (k)\n\t3.Shrink factor α\n\t" +
      "4.Number of representative points per cluster (c)\n\t5.Standard deviations from mean value (n)\n\t" +
      "6.Sample size, as percentage of the original dataset")
  } else {
    try {
      val filename = args(0)
      val k = args(1).toInt
      val alpha = args(2).toDouble
      val repr = args(3).toInt
      val n = args(4).toDouble
      val perc = args(5).toDouble
      println("Reading from input file : " + filename + " . . .")
      val points = sc.textFile(filename).map(line => new Point(line))
      println(points.count() + " elements loaded.")
      val sample = points.sample(false, perc)
      var resp: CURE.Response = null
      var pass_all: CURE.ResponseRDD = null
      spark.time({
        val cint=CURE.initializeClusters(sample.collect().toList)
        resp=CURE.cure_algorithm(cint,k,repr,alpha)
        pass_all = CURE.pass_data(points,resp.clusters,n)
      })

      val directoryName = "results/"+"p"+points.count()+",perc"+perc+",alpha"+alpha+",repr"+repr
      val directory2 = directoryName+"/repr"
      val x = spark.sparkContext.parallelize(resp.clusters.flatMap(x=>{
        x.repr.map(y=>{
          (y,x.c_id)
        })
      }))
      Utils.writeToFile(pass_all,directoryName)
      Utils.writePoints(x,directory2)
    } catch {
    case _: org.apache.hadoop.mapred.InvalidInputException => println("This file could not be found!")
    }
  }



}
