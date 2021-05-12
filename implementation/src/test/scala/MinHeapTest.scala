import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.funsuite.AnyFunSuite

class MinHeapTest extends AnyFunSuite with BeforeAndAfterAll{
  private var points:List[Point]= _
  private var sc:SparkContext = _

  override def beforeAll{
    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("CureAlgorithm")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    sc = spark.sparkContext

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    points = sc.textFile("data.txt").map(line => new Point(line)).collect().toList
  }

  override def afterAll: Unit ={
    sc.stop()
  }

  test("Initializing the Minheap"){
    val clusters = CURE.initializeClusters(points)
    val minHeap:MinHeap = new MinHeap(clusters)
    minHeap.build_heap()
    val min = minHeap.extract_min()
    assert(minHeap.size()==4)
    val closest = minHeap.get(min.closest)
    assert(minHeap.size()==4)
    if(closest.nonEmpty){
      minHeap.delete(closest.get.c_id)
    }
    assert(minHeap.size()==3)
    minHeap.delete(4)
    assert(minHeap.size()==2)
    min.distance=0.02
    minHeap.relocate(min)
    assert(minHeap.size()==3)


  }

}
