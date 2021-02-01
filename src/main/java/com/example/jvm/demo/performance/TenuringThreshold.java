package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 * 长期存活的对象将进入老年代
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms20M -Xmx20M -Xmn10M -XX:MaxTenuringThreshold=1 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.TenuringThreshold
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6447404k free), swap 7929852k(1210088k free)
 * CommandLine flags: -XX:InitialHeapSize=20971520 -XX:InitialTenuringThreshold=1 -XX:MaxHeapSize=20971520 -XX:MaxNewSize=10485760 -XX:MaxTenuringThreshold=1 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T17:32:31.114+0800: 0.119: [GC (Allocation Failure) 2021-01-30T17:32:31.114+0800: 0.119: [DefNew: 5023K->559K(9216K), 0.0043981 secs] 5023K->4655K(19456K), 0.0045328 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 2021-01-30T17:32:31.119+0800: 0.124: [GC (Allocation Failure) 2021-01-30T17:32:31.119+0800: 0.124: [DefNew: 4655K->0K(9216K), 0.0012754 secs] 8751K->4650K(19456K), 0.0013380 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 9216K, used 4336K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
 *   eden space 8192K,  52% used [0x00000000fec00000, 0x00000000ff03c340, 0x00000000ff400000)
 *   from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
 *   to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
 *  tenured generation   total 10240K, used 4650K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 *    the space 10240K,  45% used [0x00000000ff600000, 0x00000000ffa8a8c8, 0x00000000ffa8aa00, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 134480, please wait...
 * Debugger attached successfully.
 * Server compiler detected.
 * JVM version is 25.131-b11
 *
 * using thread-local object allocation.
 * Mark Sweep Compact GC
 *
 * Heap Configuration:
 *    MinHeapFreeRatio         = 40
 *    MaxHeapFreeRatio         = 70
 *    MaxHeapSize              = 20971520 (20.0MB)
 *    NewSize                  = 10485760 (10.0MB)
 *    MaxNewSize               = 10485760 (10.0MB)
 *    OldSize                  = 10485760 (10.0MB)
 *    NewRatio                 = 2
 *    SurvivorRatio            = 8
 *    MetaspaceSize            = 21807104 (20.796875MB)
 *    CompressedClassSpaceSize = 1073741824 (1024.0MB)
 *    MaxMetaspaceSize         = 17592186044415 MB
 *    G1HeapRegionSize         = 0 (0.0MB)
 *
 * Heap Usage:
 * New Generation (Eden + 1 Survivor Space):
 *    capacity = 9437184 (9.0MB)
 *    used     = 4356640 (4.154815673828125MB)
 *    free     = 5080544 (4.845184326171875MB)
 *    46.16461859809028% used
 * Eden Space:
 *    capacity = 8388608 (8.0MB)
 *    used     = 4356640 (4.154815673828125MB)
 *    free     = 4031968 (3.845184326171875MB)
 *    51.93519592285156% used
 * From Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 1048576 (1.0MB)
 *    0.0% used
 * To Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 1048576 (1.0MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 10485760 (10.0MB)
 *    used     = 4761800 (4.541206359863281MB)
 *    free     = 5723960 (5.458793640136719MB)
 *    45.41206359863281% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * `-XX:MaxTenuringThreshold=1` 设置为1 ，b1 对象在第 2 次
 * GC 时晋升到老年代，survior 空间使用为 0。
 *
 */
public class TenuringThreshold {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] b1, b2, b3;  // 定义变量
        b1 = new byte[_1MB / 4];  // 什么时候进入老年代决定于 -XX:MaxTenuringThreshold 的设置
        b2 = new byte[4 * _1MB];    // 分配 4MB 堆空间
        b3 = new byte[4 * _1MB];

        b3 = null;  // 使 b3 可以回收

        b3 = new byte[4 * _1MB];

        try {
            System.out.println("TenuringThreshold is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}