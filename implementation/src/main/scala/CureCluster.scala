import java.util.Objects.hash
import scala.util.hashing
class CureCluster (val c_id:Long, var repr:List[Point], var points:List[Point],var mean:List[Double], var closest:Long, var distance:Double) extends Comparable[CureCluster] with Serializable {

  override def compareTo(o: CureCluster): Int = {
    this.distance compare o.distance
  }

  override def equals(o: Any): Boolean = {
    o match {
      case o:CureCluster =>{
        o.isInstanceOf[CureCluster] && o.c_id==this.c_id
      }
      case o:Int =>{
        o.isInstanceOf[Int] && this.c_id==o
      }
        case _ => false
    }
  }

  override def hashCode(): Int={
      c_id.toInt*997+73
  }
}

