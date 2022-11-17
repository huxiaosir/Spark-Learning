package org.joisen.sparkStreaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable
import scala.util.Random

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream03_DIY {
  def main(args: Array[String]): Unit = {
    // todo 创建环境对象
    /**
     * StreamingContext 创建需要两个参数:
     * 第一个：环境配置
     * 第二个：批处理的周期（采集周期）
     */
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val message: ReceiverInputDStream[String] = ssc.receiverStream(new MyReceiver())
    message.print()

    // 启动采集器
    ssc.start()

    // 等待采集器的关闭
    ssc.awaitTermination()
  }

  /**
   * 自定义数据采集器
   * 1 继承Receiver，定义泛型
   * 2 重写方法
   */
  class MyReceiver extends Receiver[String](StorageLevel.MEMORY_ONLY){
    private var flag = true
    override def onStart(): Unit = {
      new Thread(new Runnable {
        override def run(): Unit = {
          while(flag){
            val message: String ="采集的数据为： " + new Random().nextInt(10).toString
            store(message)
            Thread.sleep(500)
          }
        }
      }).start()

    }

    override def onStop(): Unit = {
      flag = false
    }
  }


}
