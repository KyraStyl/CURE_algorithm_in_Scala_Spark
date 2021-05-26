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

      //points.foreach(println)

      val perc = 1
      val sample = points.sample(false, perc)
      println("Sample size = " + sample.count)
      val p = sample.take(1).head.toString
      println(p)


      var resp: CURE.Response = null
      var pass_all: CURE.ResponseRDD = null
      val alpha = 0.3
      val repr = 10
      spark.time({
//        resp = CURE.parallelCure(sample, 5, repr, alpha)
        val cint=CURE.initializeClusters(sample.collect().toList)
        resp=CURE.cure_algorithm(cint,5,repr,alpha)
        println(resp.clusters.map(x=>(x.c_id,x.points.count(_=>true),x.repr.count(_=>true))))
        pass_all = CURE.pass_data(points,resp.clusters,3)
        println(pass_all.points.count())
        println(pass_all.outliers.count())
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
