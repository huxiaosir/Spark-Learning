package org.joisen.sparkStreaming

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.Properties
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream10_MockData {
  def main(args: Array[String]): Unit = {

    // 生产模拟数据
    // 格式 ：timestamp area city userid adid
    // 含义 ：时间戳     区域  城市  用户  广告

    // Application => kafka => SparkStreaming => Analysis

    // 创建配置对象
    val prop = new Properties()
    // 添加配置
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092")
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    // 根据配置创建 Kafka 生产者
    val producer = new KafkaProducer[String, String](prop)

    while(true){
      Thread.sleep(2000)
      mockData().foreach(
        data => {
          // 向kafka中生产数据
          val record = new ProducerRecord[String, String]("joisen", data)
          producer.send(record)
          println(data)
        }
      )

    }


  }

  def mockData(): ListBuffer[String] ={
    val list: ListBuffer[String] = ListBuffer[String]()
    val areaList: ListBuffer[String] = ListBuffer[String]("华东","华南","华北")
    val cityList: ListBuffer[String] = ListBuffer[String]("北京","上海","广州")
    for( i <- 1 to new Random().nextInt(50)){
      val area: String = areaList(new Random().nextInt(3))
      val city: String = cityList(new Random().nextInt(3))
      val userId: Int = new Random().nextInt(6)
      val adId: Int = new Random().nextInt(6)
      list.append(s"${System.currentTimeMillis()} ${area} ${city} ${userId} ${adId}")
    }
    list
  }

}
