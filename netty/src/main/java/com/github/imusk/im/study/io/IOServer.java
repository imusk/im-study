package com.github.imusk.im.study.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 93806-wenzhou
 * @date 2020-07-20 10:12
 * @email iwenzhou@qq.com
 * @description 传统的IO编程-IO服务端
 * 场景：：客户端每隔两秒发送一个带有时间戳的"hello world"给服务端，服务端收到之后打印
 * https://www.jianshu.com/p/a4e03835921a
 */
public class IOServer {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8000);

        // (1) 接收新连接线程
        new Thread(() -> {
            while (true) {
                try {
                    // (1) 阻塞方法获取新的连接
                    Socket socket = serverSocket.accept();

                    // (2) 每一个新的连接都创建一个线程，负责读取数据
                    new Thread(() -> {
                        try {
                            byte[] data = new byte[1024];
                            InputStream inputStream = socket.getInputStream();
                            while (true) {
                                int len;
                                // (3) 按字节流方式读取数据
                                while ((len = inputStream.read(data)) != -1) {
                                    System.out.println(Thread.currentThread() + " - " + new String(data, 0, len));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
