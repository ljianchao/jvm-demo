package com.example.jvm.demo.service;

import java.io.IOException;

/**
 * 使用 -Xss 参数调大栈内存容量，JDK 8 默认值为 1024（K），请在 Windows 32 位系统下运行
 *
 * 启动命令：
 *     java -Xms100M -Xmx100M -Xss2M -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.service.JavaVMStackSOF
 *
 * 异常内容：
 * Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread，possibly out of memory or process/resource limits reached
 */
public class JavaVMStackOOM {

    private void loop() {
        while (true) {

        }
    }

    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(() -> {
                loop();
            });
            thread.start();
        }
    }

    public static void main(String[] args) {
        JavaVMStackOOM oom = new JavaVMStackOOM();
        oom.stackLeakByThread();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}