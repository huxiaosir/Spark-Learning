package org.joisen.sparkStreaming

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.io.{File, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream13_Req31 {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

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

    // 最近一分钟，每10秒计算一次
    // 这里设计窗口的计算
    val reduceDS: DStream[(Long, Int)] = adClickData.map(
      data => {
        val ts: Long = data.ts.toLong
        val newTs: Long = ts / 10000 * 10000
        (newTs, 1)
      }
    ).reduceByKeyAndWindow((x:Int,y:Int) => x + y, Seconds(60), Seconds(10))

//    reduceDS.print()
    reduceDS.foreachRDD(
      rdd =>{
        val list: ListBuffer[String] = ListBuffer[String]()
        val datas: Array[(Long, Int)] = rdd.sortByKey(true).collect()

        datas.foreach{
          case (time, cnt) =>{
            val timeString: String = new SimpleDateFormat("mm:ss").format(new Date(time.toLong))
            list.append(s""" { "xtime":"${timeString}", "yval":"${cnt}" }""")
          }
        }

        // 输出到文件
        val out = new PrintWriter(new FileWriter(new File("D:\\product\\JAVA\\Project\\Spark-Learning\\datas\\addclick\\adclick.json")))
        out.println("[" + list.mkString(",") + "]")

        out.flush()
        out.close()

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
