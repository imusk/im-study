package com.github.imusk.im.study.io;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @author Musk
 * @date 2020-07-20 10:15
 * @email muskcool@protonmail.com
 * @description 传统的IO编程-IO客户端
 */
public class IOClient {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8000);
                while (true) {
                    try {
                        socket.getOutputStream().write((new Date() + ": hello world").getBytes());
                        socket.getOutputStream().flush();
                        System.out.println(Thread.currentThread() + " - " + new Date() + " : 发送成功，等待 2s 下次发送");
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
