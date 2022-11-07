package org.joisen.sparkcore.rdd.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author : joisen 
 * @date : 9:31 2022/11/4 
 */
object Spark019_RDD_Pratice {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("operator")
    val sc: SparkContext = new SparkContext(sparkConf)

    // TODO 案例：统计 agent.log 文件中每个省份每种广告的点击量top3
    // 1. 获取原始数据：时间戳，省份，城市，用户，广告
    val dataRDD: RDD[String] = sc.textFile("datas/agent.log")
    // 2 将原始数据进行结构的转换，时间戳，省份，城市，用户，广告 => （（省份，广告），1）
    val mapRDD: RDD[((String, String), Int)] = dataRDD.map(
      line => {
        val datas: Array[String] = line.split(" ")
        ((datas(1), datas(4)), 1)
      }
    )

    // 3 将转换结构后的数据进行分组聚合（（省份，广告），1） => （（省份，广告），sum）
    val reduceRDD: RDD[((String, String), Int)] = mapRDD.reduceByKey(_ + _)

    // 4 将聚合的结果进行结构的转换，（（省份，广告），sum） => （省份，（广告，sum））
    val newMapRDD: RDD[(String, (String, Int))] = reduceRDD.map {
      case ((prv, ad), sum) => {
        (prv, (ad, sum))
      }
    }

    // 5 将转换结构后的数据根据省份进行分组（省份，【（广告A，sum），（广告B，sum）】）
    val groupRDD: RDD[(String, Iterable[(String, Int)])] = newMapRDD.groupByKey()

    // 6 将分组后的数据组内排序， 取前三
    val resultRDD: RDD[(String, List[(String, Int)])] = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(3)
      }
    )

    // 7 采集数据并打印
    resultRDD.collect().foreach(println)


    sc.stop()

  }

}
