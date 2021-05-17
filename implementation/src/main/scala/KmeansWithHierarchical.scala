import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.clustering.{KMeans,BisectingKMeans, KMeansModel}
import org.apache.spark.sql.SparkSession

object KmeansWithHierarchical {

  def run(): Unit ={
    val file = "data1.txt"
    val sc = SparkSession.builder.getOrCreate().sparkContext

    val csvData = sc.textFile(file)
    val VectorData = csvData.map {
      csvLine =>
        Vectors.dense( csvLine.split(',').map(_.toDouble))

    }

    val kMeans = new KMeans
    val numClusters = 5
    val maxIterations = 50
    val initializationMode = KMeans.K_MEANS_PARALLEL
    val numEpsilon = 1e-4

    kMeans.setK( numClusters )
    kMeans.setMaxIterations( maxIterations )
    kMeans.setInitializationMode( initializationMode )
    kMeans.setEpsilon( numEpsilon )

    VectorData.cache
    val kMeansModel = kMeans.run( VectorData )
    val kMeansCost = kMeansModel.computeCost( VectorData )
    println( "Input data rows : " + VectorData.count() )
    println( "K-Means Cost : " + kMeansCost )

    kMeansModel.clusterCenters.foreach{ println }

    val clusterRddInt = kMeansModel.predict( VectorData )
    val clusterCount = clusterRddInt.countByValue

    clusterCount.toList.foreach{ println }


    // Clustering the data into 6 clusters by BisectingKMeans.
    val bkm = new BisectingKMeans().setK(6)
    val model = bkm.run(VectorData)

    // Show the compute cost and the cluster centers
    println(s"Compute Cost: ${model.computeCost(VectorData)}")
    model.clusterCenters.zipWithIndex.foreach { case (center, idx) =>
      println(s"Cluster Center ${idx}: ${center}")}


  }

}
