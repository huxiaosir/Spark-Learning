package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark016_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3), ("b", 4)
    ))

    // groupByKey: 将数据源中的数据，相同key的数据分在一个组中，形成一个对偶元组
    //              元组中的第一个元素为key
    //              元组中的第二个元素就是相同key的value的集合
    val groupRdd: RDD[(String, Iterable[Int])] = rdd.groupByKey()

    groupRdd.collect().foreach(println)

    val groupRdd1: RDD[(String, Iterable[(String, Int)])] = rdd.groupBy(_._1)

    groupRdd1.collect().foreach(println)

    sc.stop()

  }

}
