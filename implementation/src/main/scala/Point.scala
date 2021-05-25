class Point (var line: String) extends Serializable{

  private val cols = line.split(",").map(_.trim())
  var values = cols.toList.map(_.toDouble)

  override def toString: String = {
    line
  }

  def equals(other: Point): Boolean = {
    val othersValues = other.values
    for (i <- values.indices){
      if (values(i)!=othersValues(i)) return false
    }
    true
  }

}