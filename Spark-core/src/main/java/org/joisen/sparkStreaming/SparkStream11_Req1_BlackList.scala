package org.joisen.sparkStreaming

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.joisen.util.JDBCUtil

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer

/**
 * @Author Joisen
 * @Date 2022/11/15 19:18
 * @Version 1.0
 */
object SparkStream11_Req1_BlackList {
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

    val ds: DStream[((String, String, String), Int)] = adClickData.transform(
      rdd => {
        // todo 周期性获取黑名单数据
        val blackList: ListBuffer[String] = ListBuffer[String]()
        val conn: Connection = JDBCUtil.getConnection
        val pstat: PreparedStatement = conn.prepareStatement("select userid from black_list")
        val rs: ResultSet = pstat.executeQuery()
        while (rs.next()) {
          blackList.append(rs.getString(1))
        }
        rs.close()
        pstat.close()
        conn.close()
        // todo 判断点击用户是否在黑名单中
        val filterRDD: RDD[AdClickData] = rdd.filter(
          data => {
            !blackList.contains(data.user)
          }
        )
        // todo 如果用户不在黑名单中，那么进行统计数量（每个采集周期）
        filterRDD.map(
          data => {
            val sdf = new SimpleDateFormat("yyyy-MM-dd")
            val day: String = sdf.format(new Date(data.ts.toLong))
            val user: String = data.user
            val ad: String = data.ad

            ((day, user, ad), 1) // word count

          }
        ).reduceByKey(_ + _)
      }
    )

    // todo 判断更新后的点击数据是否超过阈值，如果超过，那么将用户拉入到黑名单。
    ds.foreachRDD(
      rdd => {
        rdd.foreach {
          case ((day, user, ad), count) =>{
            println(s"${day} ${user} ${ad} ${count}")
            if (count >= 30){
              // todo 如果统计数量超过点击阈值，那么将用户拉入黑名单
              val conn: Connection = JDBCUtil.getConnection
              val pstat: PreparedStatement = conn.prepareStatement(
                """
                  |insert into black_list (userid) values (?)
                  |ON DUPLICATE KEY
                  |UPDATE userid = ?
                  |""".stripMargin)
              pstat.setString(1, user)
              pstat.setString(2, user)
              pstat.executeUpdate()

              pstat.close()
              conn.close()
            }else{
              // todo 如果没有超过阈值，那么需要将当天的广告点击数量进行更新
              val conn: Connection = JDBCUtil.getConnection
              val pstat: PreparedStatement = conn.prepareStatement(
                """
                  |select * from user_ad_count where dt=? and userid=? and adid=?
                  |""".stripMargin)
              pstat.setString(1, day)
              pstat.setString(2, user)
              pstat.setString(3, ad)
              // 查询统计表数据
              val rs: ResultSet = pstat.executeQuery()
              if(rs.next()){
                // 如果存在数据，那么更新
                val pstat1: PreparedStatement = conn.prepareStatement(
                  """
                    |update user_ad_count
                    |set count = count + ?
                    |where dt = ? and userid = ? and adid = ?
                    |""".stripMargin)
                pstat1.setInt(1, count)
                pstat1.setString(2, day)
                pstat1.setString(3, user)
                pstat1.setString(4, ad)
                pstat1.executeUpdate()
                pstat1.close()
                // todo 判断更新后的点击数据是否超过阈值，如果超过，那么将用户拉入到黑名单。
                val pstat2: PreparedStatement = conn.prepareStatement(
                  """
                    |select
                    |   *
                    |from user_ad_count
                    |where dt = ? and userid = ? and adid = ? and count >= 30
                    |""".stripMargin)
                pstat2.setString(1, day)
                pstat2.setString(2, user)
                pstat2.setString(3, ad)
                val rs2: ResultSet = pstat2.executeQuery()
                if(rs2.next()){
                  val pstat3: PreparedStatement = conn.prepareStatement(
                    """
                      |insert into black_list (userid) values (?)
                      |ON DUPLICATE KEY
                      |UPDATE userid = ?
                      |""".stripMargin)
                  pstat3.setString(1, user)
                  pstat3.setString(2, user)
                  pstat3.executeUpdate()
                  pstat3.close()
                }
                rs2.close()
                pstat2.close()

              }else{
                // 如果不存在数据，那么新增
                val pstat1: PreparedStatement = conn.prepareStatement(
                  """
                    |insert into user_ad_count (dt, userid, adid, count) values (?,?,?,?)
                    |""".stripMargin)
                pstat1.setString(1,day)
                pstat1.setString(2,user)
                pstat1.setString(3,ad)
                pstat1.setInt(4,count)
                pstat1.executeUpdate()
                pstat1.close()
              }
              rs.close()
              pstat.close()
              conn.close()

            }
          }
        }
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
