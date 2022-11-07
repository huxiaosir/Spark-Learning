package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark010_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- coalesce
    val rdd:RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6),3)
    // coalesce缩减分区  (不会将数据均匀分布) 想要让数据均匀分布，可以使用第二个参数shuffle
//    val newRdd: RDD[Int] = rdd.coalesce(2)
    val newRdd: RDD[Int] = rdd.coalesce(2,true)
    newRdd.saveAsTextFile("output")

    sc.stop()

  }

}
