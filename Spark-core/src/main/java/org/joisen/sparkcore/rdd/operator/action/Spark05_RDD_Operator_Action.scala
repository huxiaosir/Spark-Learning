package org.joisen.sparkcore.rdd.operator.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark05_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 1, 1, 4))
    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 4), ("b", 1)
    ),2)
    // TODO 行动算子
    rdd1.saveAsTextFile("output")
    rdd1.saveAsObjectFile("output1")
    // saveAsSequenceFile方法要求数据的格式必须为K-V类型
    rdd1.saveAsSequenceFile("output2")





    sc.stop()

  }

}
