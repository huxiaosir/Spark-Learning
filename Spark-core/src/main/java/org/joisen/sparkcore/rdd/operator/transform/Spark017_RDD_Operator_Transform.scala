package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark017_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型  aggregateByKey

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3), ("a", 4)
    ),2)

    // aggregateByKey存在函数柯里化，有两个参数列表
    // 第一个参数列表需要传递一个参数，表示为初始值：主要用于当碰见第一个key时和value进行分区内计算
    // 第二个参数列表需要传递两个参数：
    //        第一个参数表示分区内的计算规则
    //        第二个参数表示分区间的计算规则

    rdd.aggregateByKey(0)(
      (x,y) => Math.max(x,y),
      (x,y) => x+y
    ).collect().foreach(println)



    sc.stop()

  }

}
