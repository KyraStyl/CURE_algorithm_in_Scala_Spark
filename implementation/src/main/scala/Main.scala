import org.apache.log4j.{Level, Logger}

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf

object Main extends App{

  // Create spark configuration
  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("CureAlgorithm")
  val spark = SparkSession.builder().config(sparkConf).getOrCreate()
  val sc = spark.sparkContext

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  if (args.length == 0) {
    println("No arguments passed !")
  } else {
    try {
      val filename = args(0)
      println(filename)

      println("Reading from input file : " + filename + " . . .")

      val points = sc.textFile(filename).map(line => new Point(line))
      println(points.count() + " elements loaded.")

      points.foreach(println)
    } catch {
    case _: org.apache.hadoop.mapred.InvalidInputException => println("This file could not be found!")
    }
  }



}
