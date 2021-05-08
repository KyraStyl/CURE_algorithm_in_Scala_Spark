import java.util
import scala.collection.mutable
import java.util.PriorityQueue
import collection.JavaConverters._
class MinHeap(val clusters:List[CureCluster]){

//  private val pq = mutable.PriorityQueue.empty[CureCluster].reverse
  private val pq:PriorityQueue[CureCluster] = new PriorityQueue[CureCluster]();

  def build_heap():Unit = {
    pq.addAll(clusters.asJava)
  }

  /**
   * Extract min will remove the smallest element along
   * @return
   */
  def extract_min():CureCluster={
    pq.poll()
  }

  def get(id:Long):CureCluster={
    val c =pq.iterator().asScala.filter(_.c_id==id).toList.head
    this.delete(id)
    c
  }

  def delete(id:Long):Boolean = {
    pq.remove(new CureCluster(id,new Array[Point](1).toList,new Array[Double](1).toList,0,0))
  }

  def insert(c:CureCluster):Unit={
    pq.add(c)
  }

  def relocate(x:CureCluster):Unit={
    this.delete(x.c_id)
    this.insert(x)
  }

  def size():Int={
    pq.size
  }

  def getIterable():Iterable[CureCluster]={
    pq.asScala
  }





}
