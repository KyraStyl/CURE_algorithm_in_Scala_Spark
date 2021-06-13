# CURE_algorithm_in_Scala_Spark

This is a CURE implementation in Scala, using Apache Spark for scalability. The main algorithm follows the guideline from the [original paper](https://dl.acm.org/doi/abs/10.1145/276305.276312). 
There is also an outlier detection method in the last step, that finds anomaly points that deviates more than n times the standard deviation from the mean value, a statistical method that is used when the number of outlier points is unknown.
There are other 2 foldes except from the implementation. 
  * *Correctness* folder contains scripts that visualizes and evaluates the outputs of the algorithm 
  * *Data* folder contains the datasets used in the experimentation section, along with the data_generator script.

## The program takes 6 parameters:
1. Input dataset txt
2. Number of clusters (k)
3. Shrink factor α
4. Number of representative points per cluster (c)
5. Standard deviations from mean value (n)
6. Sample size, as percentage of the original dataset

## Build
Inside the implementation folder run the command:
```
sbt clean package
```
The jar file will be created in the target/scala-2.11 directory

## Execution
An example of the execution:
```
spark-submit --master local[8] curealgorithm_2.11-0.1.jar data1.txt 5 0.2 20 3.5 0.2
```

## Output
The output is consisted of 3 files. One that contains the outliers, one with the clustering results and one with the representative points per cluster. The name of the output directory is results/p **#points**,perc **sample size**,alpha **shrink factor**,repr **#repr per cluster** /
