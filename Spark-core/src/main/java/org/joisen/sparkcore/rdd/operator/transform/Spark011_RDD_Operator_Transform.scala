package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark011_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- repartition
    val rdd:RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6),2)
    // coalesce算子可以进行扩大分区的，但是需要进行shuffle操作，
//    val newRdd: RDD[Int] = rdd.coalesce(3,true)
    // 进行扩大分区操作最好使用repartition算子（底层调用coalesce算子）
    val newRdd: RDD[Int] = rdd.repartition(3)
    newRdd.saveAsTextFile("output")
    sc.stop()

  }

}
