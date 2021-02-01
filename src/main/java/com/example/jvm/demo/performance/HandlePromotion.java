package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 * 空间分配担保
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.HandlePromotion success
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6261460k free), swap 7929852k(1321252k free)
 * CommandLine flags: -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-02-01T14:13:04.596+0800: 0.101: [GC (Allocation Failure) 2021-02-01T14:13:04.596+0800: 0.101: [DefNew: 6815K->304K(9216K), 0.0030751 secs] 6815K->4400K(19456K), 0.0031933 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 2021-02-01T14:13:04.600+0800: 0.105: [GC (Allocation Failure) 2021-02-01T14:13:04.600+0800: 0.105: [DefNew: 6608K->298K(9216K), 0.0016443 secs] 10704K->6442K(19456K), 0.0016882 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 9216K, used 2653K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
 *   eden space 8192K,  28% used [0x00000000fec00000, 0x00000000fee4caa8, 0x00000000ff400000)
 *   from space 1024K,  29% used [0x00000000ff400000, 0x00000000ff44aaa8, 0x00000000ff500000)
 *   to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
 *  tenured generation   total 10240K, used 6144K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 *    the space 10240K,  60% used [0x00000000ff600000, 0x00000000ffc00030, 0x00000000ffc00200, 0x0000000100000000)
 *  Metaspace       used 2924K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 38349, please wait...
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
 *    used     = 2548864 (2.4307861328125MB)
 *    free     = 6888320 (6.5692138671875MB)
 *    27.00873480902778% used
 * Eden Space:
 *    capacity = 8388608 (8.0MB)
 *    used     = 2243032 (2.1391220092773438MB)
 *    free     = 6145576 (5.860877990722656MB)
 *    26.739025115966797% used
 * From Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 305832 (0.29166412353515625MB)
 *    free     = 742744 (0.7083358764648438MB)
 *    29.166412353515625% used
 * To Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 1048576 (1.0MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 10485760 (10.0MB)
 *    used     = 6291504 (6.0000457763671875MB)
 *    free     = 4194256 (3.9999542236328125MB)
 *    60.000457763671875% used
 *
 * 806 interned Strings occupying 54472 bytes.
 *
 * 分析：分配b4之前，由于 eden 区空间不足，且此时老年代空间足够，进行 1 次 Minor GC，
 * 回收 b1 所占内存，b2 和 b3 由于所占内存大于 survior 空间容量，晋升到老年代；
 * 分配b7之前，由于支持空间分配担保失败，且老年代的连续空间大于新生代对象总大小，进行第 2 次 Minor GC
 *
 *
 * 示例二：
 *
 * 启动命令：
 *     java -Xms20M -Xmx20M -Xmn10M -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -XX:+HeapDumpOnOutOfMemoryError -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.HandlePromotion failure
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6261060k free), swap 7929852k(1321304k free)
 * CommandLine flags: -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-02-01T14:18:23.860+0800: 0.117: [GC (Allocation Failure) 2021-02-01T14:18:23.860+0800: 0.117: [DefNew: 6815K->304K(9216K), 0.0033107 secs] 6815K->4400K(19456K), 0.0034206 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
 * 2021-02-01T14:18:23.864+0800: 0.121: [GC (Allocation Failure) 2021-02-01T14:18:23.864+0800: 0.121: [DefNew (promotion failed) : 6608K->6602K(9216K), 0.0026229 secs]2021-02-01T14:18:23.867+0800: 0.124: [Tenured: 8192K->6441K(10240K), 0.0043042 secs] 10704K->6441K(19456K), [Metaspace: 2917K->2917K(1056768K)], 0.0070045 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 9216K, used 2354K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
 *   eden space 8192K,  28% used [0x00000000fec00000, 0x00000000fee4caa8, 0x00000000ff400000)
 *   from space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
 *   to   space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
 *  tenured generation   total 10240K, used 6441K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 *    the space 10240K,  62% used [0x00000000ff600000, 0x00000000ffc4a6c8, 0x00000000ffc4a800, 0x0000000100000000)
 *  Metaspace       used 2924K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 38681, please wait...
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
 *    used     = 2243032 (2.1391220092773438MB)
 *    free     = 7194152 (6.860877990722656MB)
 *    23.768022325303818% used
 * Eden Space:
 *    capacity = 8388608 (8.0MB)
 *    used     = 2243032 (2.1391220092773438MB)
 *    free     = 6145576 (5.860877990722656MB)
 *    26.739025115966797% used
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
 *    used     = 6596296 (6.290718078613281MB)
 *    free     = 3889464 (3.7092819213867188MB)
 *    62.90718078613281% used
 *
 * 791 interned Strings occupying 53432 bytes.
 *
 * 分析：分配b4之前，由于 eden 区空间不足，且此时老年代空间足够，进行 1 次 Minor GC，
 * 回收 b1 所占内存，b2 和 b3 由于所占内存大于 survior 空间容量，晋升到老年代；
 * 分配b7之前，由于支持空间分配担保失败，但是老年代的连续空间小于新生代对象总大小，
 * 空间担保失败，进行 Full GC
 *
 */
public class HandlePromotion {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {

        if (args == null || args.length == 0 || "success".equals(args[0])) {
            handlePromotionSuccess();
        } else {
            handlePromotionFailure();
        }

        try {
            System.out.println("HandlePromotion is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JDK 8 默认支持空间分配担保失败，
     * 老年代的连续空间大于新生代对象总大小或历次晋升的平均大小（即默认支持空间分配担保失败）
     * 就会进行 Minor GC，否则将进行 Full GC。
     *
     */
    public static void handlePromotionSuccess() {
        byte[] b1, b2, b3, b4, b5, b6, b7;  // 定义变量
        b1 = new byte[2 * _1MB];
        b2 = new byte[2 * _1MB];
        b3 = new byte[2 * _1MB];
        b1 = null;

        b4 = new byte[2 * _1MB];  // 第 1 次 Minor GC
        b5 = new byte[2 * _1MB];
        b6 = new byte[2 * _1MB];
        b5 = null;
        b6 = null;

        b7 = new byte[2 * _1MB];  // 由于支持空间分配担保失败，且老年代的连续空间大于新生代对象总大小，进行第 2 次 Minor GC
    }

    /**
     * 空间担保失败，进行Full GC
     *
     */
    public static void handlePromotionFailure() {
        byte[] b1, b2, b3, b4, b5, b6, b7;  // 定义变量
        b1 = new byte[2 * _1MB];
        b2 = new byte[2 * _1MB];
        b3 = new byte[2 * _1MB];
        b1 = null;

        b4 = new byte[2 * _1MB];  // 1 次 Minor GC
        b5 = new byte[2 * _1MB];
        b6 = new byte[2 * _1MB];

        b2 = null;
        b3 = null;  // 释放b2空间，确保老年代空间能容纳

        b7 = new byte[2 * _1MB];  // 由于支持空间分配担保失败，但是老年代的连续空间小于新生代对象总大小，空间担保失败，进行 Full GC
    }
}