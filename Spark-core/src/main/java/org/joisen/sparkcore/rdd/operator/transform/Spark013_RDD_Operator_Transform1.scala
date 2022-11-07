package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark013_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- 双value类型

    val rdd1: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    val rdd2: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6),4)
    val rdd3: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6,7,8),2)
    //  Can't zip RDDs with unequal numbers of partitions: List(2, 4)
    // 两个数据源的分区数要保持一致
//    val rdd6: RDD[(Int, Int)] = rdd1.zip(rdd2)
//    println(rdd6.collect().mkString(","))

//Can only zip RDDs with same number of elements in each partition
    // 两个数据源要去分区中数据的数量要相等
    val rdd4: RDD[(Int, Int)] = rdd1.zip(rdd3)
    println(rdd4.collect().mkString(","))
    sc.stop()

  }

}
