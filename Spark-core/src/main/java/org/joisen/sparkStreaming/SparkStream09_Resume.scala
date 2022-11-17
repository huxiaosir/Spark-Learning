package org.joisen.sparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream09_Resume {
  def main(args: Array[String]): Unit = {

    val ssc: StreamingContext = StreamingContext.getActiveOrCreate("cp", () => {
      val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
      val ssc = new StreamingContext(sparkConf, Seconds(3))
      ssc.checkpoint("cp")
      val lines: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop102", 8888)
      val wordToOne: DStream[(String, Int)] = lines.map((_, 1))
      wordToOne.print()
      ssc
    })
    ssc.checkpoint("cp")

    ssc.start()
    // 等待采集器的关闭
    ssc.awaitTermination()



  }

}
