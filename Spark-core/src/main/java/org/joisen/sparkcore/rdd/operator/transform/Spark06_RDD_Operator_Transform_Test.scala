package org.joisen.sparkcore.rdd.operator.transform

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark06_RDD_Operator_Transform_Test {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 算子- groupBY
    // 统计apache.log文件中某个时间段(时间 不包含日期 )的访问量
    val rdd: RDD[String] = sc.textFile("datas/apache.log")
    val timeRdd: RDD[(String, Iterable[(String, Int)])] = rdd.map(
      (line: String) => {
        val datas: Array[String] = line.split(" ")
        val time: String = datas(3)
        val sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss")
        val date: Date = sdf.parse(time)
        val sdf1 = new SimpleDateFormat("HH")
        val hour: String = sdf1.format(date)
        (hour, 1)
      }
    ).groupBy(_._1)
    timeRdd.map{
      case (hour, iter) => {
        (hour, iter.size)
      }
    }.collect().foreach(println)
    sc.stop()

  }

}
