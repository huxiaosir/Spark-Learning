package org.joisen.sparkcore.rdd.operator.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark03_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)

    // TODO 行动算子
    // aggregateByKey: 初始值只会参与分区内的计算
    // aggregate: 初始值会参与分区内计算，也会参与分区间的计算
//    val res: Int = rdd.aggregate(10)(_ + _, _ + _)
//    println(res)

    // fold: 当aggregate的分区间和分区内的计算规则一致时可以用fold代替
    val res: Int = rdd.fold(10)(_ + _)
    println(res)



    sc.stop()

  }

}
