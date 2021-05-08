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
    val points = sc.textFile("data.txt").map(line => new Point(line))

    val clusters = CURE.initializeClusters(points.collect().toList)
    print("Hey")


  }
}
