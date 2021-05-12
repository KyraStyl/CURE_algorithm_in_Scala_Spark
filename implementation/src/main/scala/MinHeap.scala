import java.util
import scala.collection.mutable
import java.util.PriorityQueue
import collection.JavaConverters._
class MinHeap(val clusters:List[CureCluster]){

//  private val pq = mutable.PriorityQueue.empty[CureCluster].reverse
  private val pq:PriorityQueue[CureCluster] = new PriorityQueue[CureCluster]();
  private val map:mutable.HashMap[Long,CureCluster] = new mutable.HashMap[Long,CureCluster]();

  def build_heap():Unit = {
    pq.addAll(clusters.asJava)
    clusters.foreach(cluster=>{
      map(cluster.c_id)=cluster
    })
  }

  /**
   * Extract min will remove the smallest element along
   * @return
   */
  def extract_min():CureCluster={
    val cluster = pq.poll()
    map.remove(cluster.c_id)
    cluster
  }

  def get(id:Long):Option[CureCluster]={
    map.get(id)
  }

  def delete(id:Long):Boolean = {
    var removed = false
    if(map.contains(id)){
      val c = map.remove(id)
      removed = pq.remove(c.getOrElse(new CureCluster(id,new Array[Point](1).toList,new Array[Point](1).toList,new Array[Double](1).toList,0,0)))
    }
    removed
  }

  def insert(c:CureCluster):Unit={
    pq.add(c)
    map(c.c_id)=c
  }

  def relocate(x:CureCluster):Unit={
    this.delete(x.c_id)
    this.insert(x)
    if(map.contains(x.c_id)) {
      map.update(x.c_id, x)
    }else{
      map(x.c_id)=x
    }
  }

  def size():Int={
    map.size
  }

  def getIterable():Iterable[CureCluster]={
    map.values
  }





}
