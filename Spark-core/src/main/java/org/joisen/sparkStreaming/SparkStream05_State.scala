package org.joisen.sparkStreaming

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream05_State {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))
    ssc.checkpoint("cp")

    // 无状态数据操作，只对当前的采集周期内的数据进行处理
    // 在某些场合下，需要保留数据统计结果（状态），实现数据的汇总
    val datas: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 8888)
    val wordToOne: DStream[(String, Int)] = datas.map((_, 1))
//    val wordToCount: DStream[(String, Int)] = wordToOne.reduceByKey(_ + _)
//    wordToCount.print()
    // updateStateByKey: 根据key对数据的状态进行更新
    // 传递的参数有两个值： 第一个值表示相同的key的value数据
    //                   第二个值表示缓冲区相同key的value数据
    val state: DStream[(String, Int)] = wordToOne.updateStateByKey(
      (seq: Seq[Int], buff: Option[Int]) => {
        val newCount: Int = buff.getOrElse(0) + seq.sum
        Option(newCount)
      }
    )

    state.print()


    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()
  }

}
