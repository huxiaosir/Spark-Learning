package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark06_RDD_Operator_Transform {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- groupBY
    val rdd:RDD[Int] = sc.makeRDD(List(1,2,3,4),2)
    // groupBy会将数据源中的每一个数据进行分组判断，根据返回的分组key进行分组，
    // 相同的key值的数据回放置在一个组中
    def groupFunc(num: Int) = {
      num % 2
    }

    val groupRdd: RDD[(Int, Iterable[Int])] = rdd.groupBy(groupFunc)
    groupRdd.collect().foreach(println)

    sc.stop()

  }

}
