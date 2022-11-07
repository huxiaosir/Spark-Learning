package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark03_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子-mapPartitionsWithIndex
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    val mapiRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex(
      (index, iter) => {
        iter.map(
          num => {
            (index, num) // 列出数据所在的分区
          }
        )
      }
    )
    mapiRdd.collect().foreach(println)



    sc.stop()

  }

}
