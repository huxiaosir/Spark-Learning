package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark06_RDD_Operator_Transform1 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- groupBY
    val rdd= sc.makeRDD(List("Hello","Spark","Scala","Hadoop"),2)
    // 分区和分组没有必然的关系
    val groupRdd: RDD[(Char, Iterable[String])] = rdd.groupBy(_.charAt(0))
    groupRdd.collect().foreach(println)
    sc.stop()

  }

}
