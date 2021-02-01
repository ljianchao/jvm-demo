package com.example.jvm.demo.service;

import java.io.IOException;

/**
 * 使用 -Xss 参数减小栈内存容量，JDK 8 默认值为 1024（K）
 * <p>
 * 启动命令：
 * java -Xms100M -Xmx100M -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.service.JavaVMStackSOFWithManyVars
 * <p>
 * 异常内容：
 * Exception in thread "Thread-0" java.lang.StackOverflowError
 * 	at com.example.jvm.demo.service.JavaVMStackSOFWithManyVars.test(JavaVMStackSOFWithManyVars.java:47)
 * 	at com.example.jvm.demo.service.JavaVMStackSOFWithManyVars.test(JavaVMStackSOFWithManyVars.java:47)
 * 	at com.example.jvm.demo.service.JavaVMStackSOFWithManyVars.test(JavaVMStackSOFWithManyVars.java:47)
 * 	at com.example.jvm.demo.service.JavaVMStackSOFWithManyVars.test(JavaVMStackSOFWithManyVars.java:47)
 * 	at com.example.jvm.demo.service.JavaVMStackSOFWithManyVars.test(JavaVMStackSOFWithManyVars.java:47)
 */
public class JavaVMStackSOFWithManyVars implements Runnable {

    private int stackLenth = 1;

    public void test() {
        long unused1, unused2, unused3, unused4, unused5,
                unused6, unused7, unused8, unused9, unused10,
                unused11, unused12, unused13, unused14, unused15,
                unused16, unused17, unused18, unused19, unused20,
                unused21, unused22, unused23, unused24, unused25,
                unused26, unused27, unused28, unused29, unused30,
                unused31, unused32, unused33, unused34, unused35,
                unused36, unused37, unused38, unused39, unused40,
                unused41, unused42, unused43, unused44, unused45,
                unused46, unused47, unused48, unused49, unused50,
                unused51, unused52, unused53, unused54, unused55,
                unused56, unused57, unused58, unused59, unused60,
                unused61, unused62, unused63, unused64, unused65,
                unused66, unused67, unused68, unused69, unused70,
                unused71, unused72, unused73, unused74, unused75,
                unused76, unused77, unused78, unused79, unused80,
                unused81, unused82, unused83, unused84, unused85,
                unused86, unused87, unused88, unused89, unused90,
                unused91, unused92, unused93, unused94, unused95,
                unused96, unused97, unused98, unused99, unused100;

        stackLenth++;

        test();

        unused1 = unused2 = unused3 = unused4 = unused5 =
                unused6 = unused7 = unused8 = unused9 = unused10 =
                        unused11 = unused12 = unused13 = unused14 = unused15 =
                                unused16 = unused17 = unused18 = unused19 = unused20 =
                                        unused21 = unused22 = unused23 = unused24 = unused25 =
                                                unused26 = unused27 = unused28 = unused29 = unused30 =
                                                        unused31 = unused32 = unused33 = unused34 = unused35 =
                                                                unused36 = unused37 = unused38 = unused39 = unused40 =
                                                                        unused41 = unused42 = unused43 = unused44 = unused45 =
                                                                                unused46 = unused47 = unused48 = unused49 = unused50 =
                                                                                        unused51 = unused52 = unused53 = unused54 = unused55 =
                                                                                                unused56 = unused57 = unused58 = unused59 = unused60 =
                                                                                                        unused61 = unused62 = unused63 = unused64 = unused65 =
                                                                                                                unused66 = unused67 = unused68 = unused69 = unused70 =
                                                                                                                        unused71 = unused72 = unused73 = unused74 = unused75 =
                                                                                                                                unused76 = unused77 = unused78 = unused79 = unused80 =
                                                                                                                                        unused81 = unused82 = unused83 = unused84 = unused85 =
                                                                                                                                                unused86 = unused87 = unused88 = unused89 = unused90 =
                                                                                                                                                        unused91 = unused92 = unused93 = unused94 = unused95 =
                                                                                                                                                                unused96 = unused97 = unused98 = unused99 = unused100 = 0;
    }

    @Override
    public void run() {
        try {
            test();
        } catch (Error e) {
            System.out.println("stack length: " + stackLenth);
            throw e;
        }
    }

    public static void main(String[] args) {
        // 测试栈容量溢出
        new Thread(new JavaVMStackSOFWithManyVars()).start();

        System.out.println("JavaVMStackSOFWithManyVars is up.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}