package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark014_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    val mapRdd: RDD[(Int, Int)] = rdd.map((_, 1))

    // partitionBy根据指定的分区规则对数据进行重分区
    mapRdd.partitionBy(new HashPartitioner(2)).saveAsTextFile("output")


    sc.stop()

  }

}
