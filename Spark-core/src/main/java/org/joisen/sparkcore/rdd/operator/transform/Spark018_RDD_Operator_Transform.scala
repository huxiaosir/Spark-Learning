package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark018_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型  aggregateByKey

    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("b", 2), ("c", 3)
    ))
    val rdd2: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 4), ("b", 5), ("c", 6)
    ))
    // join: 两个不同数据源的数据，相同的key的value会连接在一起形成元组（没有相同key的数据不会出现）
    val joinRdd: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
    joinRdd.collect().foreach(println)

    println("============")

    // coGroup:connect+ group (分组连接)
    val cgRdd: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd1.cogroup(rdd2)
    cgRdd.collect().foreach(println)
    sc.stop()

  }

}
