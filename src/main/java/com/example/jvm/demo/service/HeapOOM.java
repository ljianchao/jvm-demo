package com.example.jvm.demo.service;

import java.io.IOException;
import java.util.Vector;

/**
 * jvm 最大堆内存需要小于10M，将堆的最小值 `-Xms` 参数与最大值 `-Xmx` 参数设置为一样避免堆自动扩展；
 * `-XX:+HeapDumpOnOutOfMemoryError` 参数让虚拟机在出现内存溢出异常的时候 Dump 出当前的内存转储
 * 快照以便进行时候分析，默认的堆转储文件为 `./java_pid%p.hprof`
 *
 * 启动命令：
 *     java -Xms5M -Xmx5M -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.service.HeapOOM
 *
 * 异常内容：
 * java.lang.OutOfMemoryError: Java heap space
 * Dumping heap to java_pid61446.hprof ...
 * Heap dump file created [6156134 bytes in 0.015 secs]
 * Exception in thread "Thread-0" java.lang.OutOfMemoryError: Java heap space
 * 	at com.example.jvm.demo.service.HeapOOM.testOOM(HeapOOM.java:28)
 * 	at com.example.jvm.demo.service.HeapOOM.run(HeapOOM.java:36)
 * 	at java.lang.Thread.run(Thread.java:748)
 */
public class HeapOOM implements Runnable {

    public void testOOM() {
        Vector vector = new Vector();
        for (int i = 0; i < 10; i++) {
            byte[] bytes = new byte[1024 * 1024];   // 每个循环分配 1MB 内存
            vector.add(bytes);  // 强引用，使 GC 时不能释放空间
            System.out.println(i + "MB is allocated.");
        }
    }

    @Override
    public void run() {
        testOOM();
    }

    public static void main(String[] args) {

        // 测试栈容量溢出
        new Thread(new HeapOOM()).start();

        System.out.println("HeapOOM is up.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}