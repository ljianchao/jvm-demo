package com.example.jvm.demo.service;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * `-XX:MaxDirectMemorySize` 参数设置直接内存（Direct Memory）的容量大小
 *
 * 启动命令：
 *     java -Xms20M -Xmx20M -XX:MaxDirectMemorySize=10M -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.service.DirectMemoryOOM
 *
 * 异常内容：
 * Exception in thread "main" java.lang.OutOfMemoryError
 * 	at sun.misc.Unsafe.allocateMemory(Native Method)
 * 	at com.example.jvm.demo.service.DirectMemoryOOM.main(DirectMemoryOOM.java:28)
 */
public class DirectMemoryOOM {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {

        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }
}