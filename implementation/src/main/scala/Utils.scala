object Utils {

  def euclideanDistanceP(p1:Point,p2:Point):Double={
    this.euclideanDistance(p1.values,p2.values)
  }

  def euclideanDistance(p1:List[Double],p2:List[Double]):Double={
    Math.sqrt(p1.zip(p2).map(pair=>{
      Math.pow(pair._1-pair._2,2)
    }).sum)
  }

  def distanceClusters(c1:CureCluster,c2:CureCluster):Double={
    c1.repr.flatMap(p1=>{
      c2.repr.map(p2=>{
        this.euclideanDistanceP(p1,p2)
      })
    }).min
  }


}
