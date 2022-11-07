package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark01_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子-map
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    // 转换函数
    def mapFunc(num: Int): Int = {
      num * 2
    }
//    val mapRdd: RDD[Int] = rdd.map(mapFunc)
//    val mapRdd: RDD[Int] = rdd.map((num: Int) => {num*2})
    val mapRdd: RDD[Int] = rdd.map(_*2)
    mapRdd.collect().foreach(println)


    sc.stop()

  }

}
