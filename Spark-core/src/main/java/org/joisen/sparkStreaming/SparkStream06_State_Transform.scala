package org.joisen.sparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream06_State_Transform {
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
    // transform方法可以将底层的RDD获取到后进行操作
    // code: Driver端
    val newDS: DStream[String] = lines.transform(
      rdd => {
        // code: Driver端 (周期性执行)
        rdd.map(
          str => {
            // code: Executor
            str
          }
        )
      }
    )
    // code: Driver端
    val newDS1: DStream[String] = lines.map(
      data =>{
        // code: Executor
        data
      }
    )

    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()
  }

}
