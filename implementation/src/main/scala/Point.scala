class Point (var line: String) extends Serializable{

  private val cols = line.split(",").map(_.trim())
  val values = cols.toList.map(_.toFloat)

  override def toString: String = {
    val str="Element : "+values.toString()+""
    str
  }

  def equals(other: Point): Boolean = {
    val othersValues = other.values
    for (i <- values.indices){
      if (values(i)!=othersValues(i)) return false
    }
    true
  }

}