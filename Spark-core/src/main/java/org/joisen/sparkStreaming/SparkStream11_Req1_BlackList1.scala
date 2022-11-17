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
object SparkStream11_Req1_BlackList1 {
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
        // rdd的foreach方法会每一条数据都创建连接
        // foreach方法是RDD算子，算子之外的代码是在Driver端执行，而算子内的代码是在Executor端执行
        // 如果将创建连接操作提带算子外，就会涉及闭包操作，需要进行序列化，而数据库的连接对象是不能序列化的
        // RDD提供了一个算子可以提升效率 foreachPartition
        // 可以一个分区创建一个连接对象，这样可以大幅度减少连接对象的数量，提升效率
//        rdd.foreachPartition(
//          iter =>{
//            iter.foreach{
//              case ((day, user, ad), count) =>{
//                println(s"${day} ${user} ${ad} ${count}")
//                if (count >= 30) {
//                  // todo 如果统计数量超过点击阈值，那么将用户拉入黑名单
//                  val conn: Connection = JDBCUtil.getConnection
//                  val sql: String =
//                    """
//                      |insert into black_list (userid) values (?)
//                      |ON DUPLICATE KEY
//                      |UPDATE userid = ?
//                      |""".stripMargin
//                  JDBCUtil.executeUpdate(conn, sql, Array(user, user))
//                  conn.close()
//                } else {
//                  // todo 如果没有超过阈值，那么需要将当天的广告点击数量进行更新
//                  val conn: Connection = JDBCUtil.getConnection
//                  val sql = "select * from user_ad_count where dt=? and userid=? and adid=?"
//                  // 查询统计表数据
//                  val flag: Boolean = JDBCUtil.isExist(conn, sql, Array(day, user, ad))
//
//                  if (flag) {
//                    // 如果存在数据，那么更新
//                    val sql1: String =
//                      """
//                        |update user_ad_count
//                        |set count = count + ?
//                        |where dt = ? and userid = ? and adid = ?
//                        |""".stripMargin
//                    JDBCUtil.executeUpdate(conn, sql1, Array(count, day, user, ad))
//                    // todo 判断更新后的点击数据是否超过阈值，如果超过，那么将用户拉入到黑名单。
//                    val sql4 =
//                      """
//                        |select
//                        |   *
//                        |from user_ad_count
//                        |where dt = ? and userid = ? and adid = ? and count >= 30
//                        |""".stripMargin
//                    // 查询统计表数据
//                    val flag1: Boolean = JDBCUtil.isExist(conn, sql4, Array(day, user, ad))
//
//                    if (flag1) {
//                      val sql2: String =
//                        """
//                          |insert into black_list (userid) values (?)
//                          |ON DUPLICATE KEY
//                          |UPDATE userid = ?
//                          |""".stripMargin
//                      JDBCUtil.executeUpdate(conn, sql2, Array(user, user))
//                    }
//
//                  } else {
//                    // 如果不存在数据，那么新增
//                    val sql3: String =
//                      """
//                        |insert into user_ad_count (dt, userid, adid, count) values (?,?,?,?)
//                        |""".stripMargin
//                    JDBCUtil.executeUpdate(conn, sql3, Array(day, user, ad, count))
//                  }
//                  conn.close()
//                }
//              }
//            }
//          }
//        )



        rdd.foreach {
          case ((day, user, ad), count) =>{
            println(s"${day} ${user} ${ad} ${count}")
            if (count >= 30){
              // todo 如果统计数量超过点击阈值，那么将用户拉入黑名单
              val conn: Connection = JDBCUtil.getConnection
              val sql: String =
                """
                  |insert into black_list (userid) values (?)
                  |ON DUPLICATE KEY
                  |UPDATE userid = ?
                  |""".stripMargin
              JDBCUtil.executeUpdate(conn,sql,Array(user,user))
              conn.close()
            }else{
              // todo 如果没有超过阈值，那么需要将当天的广告点击数量进行更新
              val conn: Connection = JDBCUtil.getConnection
              val sql = "select * from user_ad_count where dt=? and userid=? and adid=?"
              // 查询统计表数据
              val flag: Boolean = JDBCUtil.isExist(conn, sql, Array(day, user, ad))

              if(flag){
                // 如果存在数据，那么更新
                val sql1: String =
                  """
                    |update user_ad_count
                    |set count = count + ?
                    |where dt = ? and userid = ? and adid = ?
                    |""".stripMargin
                JDBCUtil.executeUpdate(conn,sql1,Array(count,day,user,ad))
                // todo 判断更新后的点击数据是否超过阈值，如果超过，那么将用户拉入到黑名单。
                val sql4 =
                  """
                    |select
                    |   *
                    |from user_ad_count
                    |where dt = ? and userid = ? and adid = ? and count >= 30
                    |""".stripMargin
                // 查询统计表数据
                val flag1: Boolean = JDBCUtil.isExist(conn, sql4, Array(day, user, ad))

                if(flag1){
                  val sql2: String =
                    """
                      |insert into black_list (userid) values (?)
                      |ON DUPLICATE KEY
                      |UPDATE userid = ?
                      |""".stripMargin
                  JDBCUtil.executeUpdate(conn,sql2,Array(user,user))
                }

              }else{
                // 如果不存在数据，那么新增
                val sql3: String =
                  """
                    |insert into user_ad_count (dt, userid, adid, count) values (?,?,?,?)
                    |""".stripMargin
               JDBCUtil.executeUpdate(conn,sql3,Array(day,user,ad,count))
              }
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
