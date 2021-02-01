package com.example.jvm.demo.service;

import java.io.IOException;

/**
 * 使用 -Xss 参数减小栈内存容量，JDK 8 默认值为 1024（K）
 *
 * 启动命令：
 *     java -Xms100M -Xmx100M -Xss228K -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.service.JavaVMStackSOF
 *
 * 异常内容：
 * Exception in thread "Thread-0" java.lang.StackOverflowError
 * 	at com.example.jvm.demo.service.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:25)
 * 	at com.example.jvm.demo.service.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:25)
 * 	at com.example.jvm.demo.service.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:25)
 * 	at com.example.jvm.demo.service.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:25)
 * 	at com.example.jvm.demo.service.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:25)
 */
public class JavaVMStackSOF implements Runnable {

    private int stackLenth = 1;

    public void stackLeak() {
        stackLenth++;
        stackLeak();
    }

    @Override
    public void run() {
        this.stackLeak();
    }

    public static void main(String[] args) {

        // 测试栈容量溢出
        new Thread(new JavaVMStackSOF()).start();

        System.out.println("JavaVMStackSOF is up.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}