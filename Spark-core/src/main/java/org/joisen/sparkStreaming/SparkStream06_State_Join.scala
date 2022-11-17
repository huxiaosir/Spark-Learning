package org.joisen.sparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream06_State_Join {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(5))


    val datas8888: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 8888)
    val datas8889: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop103", 8889)

    val map8888: DStream[(String, Int)] = datas8888.map((_, 8))
    val map8889: DStream[(String, Int)] = datas8888.map((_, 9))

    // DStream的Join操作 就是两个RDD的join
    val joinDS: DStream[(String, (Int, Int))] = map8888.join(map8889)

    joinDS.print()
    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()
  }

}
