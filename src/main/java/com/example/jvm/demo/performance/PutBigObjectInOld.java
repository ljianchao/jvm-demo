package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 * 大对象直接分配到老年代
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -XX:PretenureSizeThreshold=3145728 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutBigObjectInOld
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6449836k free), swap 7929852k(1208500k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:PretenureSizeThreshold=3145728 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * Heap
 *  def new generation   total 18432K, used 1639K [0x00000000fc400000, 0x00000000fd800000, 0x00000000fd800000)
 *   eden space 16384K,  10% used [0x00000000fc400000, 0x00000000fc599d08, 0x00000000fd400000)
 *   from space 2048K,   0% used [0x00000000fd400000, 0x00000000fd400000, 0x00000000fd600000)
 *   to   space 2048K,   0% used [0x00000000fd600000, 0x00000000fd600000, 0x00000000fd800000)
 *  tenured generation   total 40960K, used 16384K [0x00000000fd800000, 0x0000000100000000, 0x0000000100000000)
 *    the space 40960K,  40% used [0x00000000fd800000, 0x00000000fe800040, 0x00000000fe800200, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 132339, please wait...
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
 *    MaxHeapSize              = 62914560 (60.0MB)
 *    NewSize                  = 20971520 (20.0MB)
 *    MaxNewSize               = 20971520 (20.0MB)
 *    OldSize                  = 41943040 (40.0MB)
 *    NewRatio                 = 2
 *    SurvivorRatio            = 8
 *    MetaspaceSize            = 21807104 (20.796875MB)
 *    CompressedClassSpaceSize = 1073741824 (1024.0MB)
 *    MaxMetaspaceSize         = 17592186044415 MB
 *    G1HeapRegionSize         = 0 (0.0MB)
 *
 * Heap Usage:
 * New Generation (Eden + 1 Survivor Space):
 *    capacity = 18874368 (18.0MB)
 *    used     = 1006760 (0.9601211547851562MB)
 *    free     = 17867608 (17.039878845214844MB)
 *    5.33400641547309% used
 * Eden Space:
 *    capacity = 16777216 (16.0MB)
 *    used     = 1006760 (0.9601211547851562MB)
 *    free     = 15770456 (15.039878845214844MB)
 *    6.000757217407227% used
 * From Space:
 *    capacity = 2097152 (2.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 2097152 (2.0MB)
 *    0.0% used
 * To Space:
 *    capacity = 2097152 (2.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 2097152 (2.0MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 41943040 (40.0MB)
 *    used     = 16777280 (16.00006103515625MB)
 *    free     = 25165760 (23.99993896484375MB)
 *    40.000152587890625% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * 4个 4MB 对象直接分配在老年代
 *
 */
public class PutBigObjectInOld {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] b1, b2, b3, b4;  // 定义变量
        b1 = new byte[4 * _1MB];  // 分配4MB堆空间，考察堆空间的使用情况
        b2 = new byte[4 * _1MB];
        b3 = new byte[4 * _1MB];
        b4 = new byte[4 * _1MB];

        try {
            System.out.println("PutBigObjectInOld is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}