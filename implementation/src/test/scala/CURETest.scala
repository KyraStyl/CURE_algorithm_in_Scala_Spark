import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.scalatest.funsuite.AnyFunSuite

class CURETest extends AnyFunSuite {
  test("Transform points to cluster") {
    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("CureAlgorithm")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val points = sc.textFile("data1.txt").map(line => new Point(line))
    val perc = 0.05
    val sample = points.sample(false, perc)

    val init_Clusters = CURE.initializeClusters(sample.collect().toList)
    val original_clusters = CURE.cure_algorithm(init_Clusters,5,5,0.2)
    val pass_all = CURE.pass_data(points,original_clusters.clusters,3) //outliers 3 times the stdev from mean
    print("Hey")

  }

}
