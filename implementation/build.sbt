name := "cureAlgorithm"

version := "0.1"
scalaVersion := "2.11.12"
val sparkVersion = "2.4.7"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion
)