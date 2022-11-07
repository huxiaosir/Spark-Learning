package org.joisen.sparkcore.test

import java.io.{ObjectOutputStream, OutputStream}
import java.net.Socket

/**
 * @author : joisen 
 * @date : 10:22 2022/11/3 
 */
object Driver {
  def main(args: Array[String]): Unit = {
    // 连接服务器
    val client: Socket = new Socket("localhost",9999)
    val client1: Socket = new Socket("localhost",8888)


    val task: Task = new Task
    // 获取输出流
    val out: OutputStream = client.getOutputStream
    val objOut: ObjectOutputStream = new ObjectOutputStream(out)
    val subTask: SubTask = new SubTask
    subTask.logic = task.logic
    subTask.datas = task.datas.take(2)

    objOut.writeObject(subTask)
    objOut.flush()
    objOut.close()
    client.close()


    // 获取输出流
    val out1: OutputStream = client1.getOutputStream
    val objOut1: ObjectOutputStream = new ObjectOutputStream(out1)
    val subTask1: SubTask = new SubTask
    subTask1.logic = task.logic
    subTask1.datas = task.datas.takeRight(2)
    objOut1.writeObject(subTask1)

    objOut1.flush()
    objOut1.close()
    client1.close()


    println("客户端发送数据结束...")
  }
}
