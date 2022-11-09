package org.joisen.sparkcore.framework.service
import org.apache.spark.rdd.RDD

import org.joisen.sparkcore.framework.common.TService
import org.joisen.sparkcore.framework.dao.WordCountDao
/**
 * @author : joisen 
 * @date : 9:20 2022/11/9 
 */
/**
 * 服务层
 */
class WordCountService extends TService {
  private val wordCountDao = new WordCountDao();


  // 数据分析
  def dataAnalysis() ={
    val lines: RDD[String] = wordCountDao.readFile("datas/word.txt")
    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String, Int)] = words.map(
      word => (word, 1)
    )
    // spark框架提供了更多的功能，可以将分组和聚合使用一个方法实现
    // reduceByKey：相同key的数据可以对value进行聚合
    val wordToCount: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)


    // 5 将转换结果采集到控制台打印出来
    val array: Array[(String, Int)] = wordToCount.collect()
    array
  }
}
