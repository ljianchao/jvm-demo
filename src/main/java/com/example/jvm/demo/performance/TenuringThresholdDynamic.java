package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 * 动态对象年龄判定
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms20M -Xmx20M -Xmn10M -XX:MaxTenuringThreshold=15 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.TenuringThresholdDynamic
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6445968k free), swap 7929852k(1210592k free)
 * CommandLine flags: -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:MaxNewSize=10485760 -XX:MaxTenuringThreshold=15 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T17:45:01.896+0800: 0.117: [GC (Allocation Failure) 2021-01-30T17:45:01.896+0800: 0.117: [DefNew: 5279K->815K(9216K), 0.0038975 secs] 5279K->4911K(19456K), 0.0040133 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 2021-01-30T17:45:01.901+0800: 0.121: [GC (Allocation Failure) 2021-01-30T17:45:01.901+0800: 0.121: [DefNew: 4911K->0K(9216K), 0.0011728 secs] 9007K->4906K(19456K), 0.0012285 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 9216K, used 4337K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
 *   eden space 8192K,  52% used [0x00000000fec00000, 0x00000000ff03c448, 0x00000000ff400000)
 *   from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
 *   to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
 *  tenured generation   total 10240K, used 4906K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 *    the space 10240K,  47% used [0x00000000ff600000, 0x00000000ffaca8f8, 0x00000000ffacaa00, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 135189, please wait...
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
 *    used     = 4356904 (4.155067443847656MB)
 *    free     = 5080280 (4.844932556152344MB)
 *    46.16741604275174% used
 * Eden Space:
 *    capacity = 8388608 (8.0MB)
 *    used     = 4356904 (4.155067443847656MB)
 *    free     = 4031704 (3.8449325561523438MB)
 *    51.9383430480957% used
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
 *    used     = 5023992 (4.791252136230469MB)
 *    free     = 5461768 (5.208747863769531MB)
 *    47.91252136230469% used
 *
 * 803 interned Strings occupying 54256 bytes.
 *
 * 分析：
 * `-XX:MaxTenuringThreshold=1` 设置为15 ，b1 和 b2 对象在第 2 次
 * GC 时晋升到老年代，因为 b1 和 b2 对象所占空间已达 Survior 空间的一半，
 * 且它们同龄，survior 空间使用为 0。
 *
 */
public class TenuringThresholdDynamic {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] b1, b2, b3, b4;  // 定义变量
        b1 = new byte[_1MB / 4];
        b2 = new byte[_1MB / 4];  // b1 + b2 大于 survior 空间的一半
        b3 = new byte[4 * _1MB];
        b4 = new byte[4 * _1MB];
        b4 = null;  // 使 b3 可以回收

        b4 = new byte[4 * _1MB];

        try {
            System.out.println("TenuringThresholdDynamic is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}