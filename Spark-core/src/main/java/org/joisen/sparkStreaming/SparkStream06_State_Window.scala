package org.joisen.sparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream06_State_Window {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 8888)
    val wordToOne: DStream[(String, Int)] = lines.map((_, 1))

    // 窗口的范围应该是采集周期的整数倍
    // 窗口可以滑动，但默认情况下，一个采集周期进行滑动
    // 可能会出现重复数据的计算，为了避免这种情况，可以改变滑动的幅度（步长）
    val windowDS: DStream[(String, Int)] = wordToOne.window(Seconds(6),Seconds(6))

    val wordToCount: DStream[(String, Int)] = windowDS.reduceByKey(_ + _)

    wordToCount.print()
    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()
  }

}
