package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark012_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- sortby
    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("1", 1), ("11", 2), ("2", 3)), 2)
    // sortBy方法可以根据指定的规则对数据源中的数据进行排序，默认为升序
    val newRdd: RDD[(String, Int)] = rdd.sortBy(t => t._1.toInt,false)

    newRdd.collect().foreach(println)


    sc.stop()

  }

}
