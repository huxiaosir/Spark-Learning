package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark015_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- Key-Value类型

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3), ("b", 4)
    ))

    // reduceByKey: 相同key的数据进行value数据的聚合操作
    // scala语言中一般的聚合操作都是两两聚合 spark亦是
    val reduceRdd: RDD[(String, Int)] = rdd.reduceByKey((x: Int, y: Int) => {
      println(s"x: ${x},,,y:${y}")
      x + y
    })
    reduceRdd.collect().foreach(println)


    sc.stop()

  }

}
