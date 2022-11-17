package org.joisen.sparkStreaming

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.joisen.sparkStreaming.SparkStream11_Req1_BlackList.AdClickData
import org.joisen.util.JDBCUtil

import java.sql.{Connection, PreparedStatement}
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream12_Req2 {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // 定义kafka参数
    val kafkaPara: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG ->
        "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "joisen",
      "key.deserializer" ->
        "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" ->
        "org.apache.kafka.common.serialization.StringDeserializer"
    )
    val kafkaDataDS: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent, // 位置策略
      ConsumerStrategies.Subscribe[String, String](Set("joisen"), kafkaPara)
    )
    // 从kafka中获取数据
    val adClickData: DStream[AdClickData] = kafkaDataDS.map(
      kafkaData => {
        val data: String = kafkaData.value()
        val datas: Array[String] = data.split(" ")
        AdClickData(datas(0), datas(1), datas(2), datas(3), datas(4))
      }
    )

    val reduceDS: DStream[((String, String, String, String), Int)] = adClickData.map(
      data => {
        val sdf = new SimpleDateFormat("yyyy-MM-dd")
        val day: String = sdf.format(new Date(data.ts.toLong))
        val area: String = data.area
        val city: String = data.city
        val ad: String = data.ad
        ((day, area, city, ad), 1)
      }
    ).reduceByKey(_ + _)

    reduceDS.foreachRDD(
      rdd =>{
        rdd.foreachPartition(
          iter => {
            val conn: Connection = JDBCUtil.getConnection
            val pstat: PreparedStatement = conn.prepareStatement(
              """
                |insert into area_city_ad_count (dt,area,city,adid,count)
                |values (?,?,?,?,?)
                |ON DUPLICATE KEY
                |UPDATE count = count + ?
                |""".stripMargin)
            iter.foreach{
              case ((day, area, city, ad), sum) => {
                pstat.setString(1,day)
                pstat.setString(2,area)
                pstat.setString(3,city)
                pstat.setString(4,ad)
                pstat.setInt(5,sum)
                pstat.setInt(6,sum)
                pstat.executeUpdate()
              }
            }
            pstat.close()
            conn.close()
          }
        )
      }
    )




    // 启动采集器
    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()
  }

  // 广告点击数据
  case class AdClickData(ts: String, area: String, city: String, user: String, ad: String)
}
