package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 *
 * 将新对象预留在新生代
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -Xmn10M -XX:SurvivorRatio=8 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden2
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6457420k free), swap 7929852k(1203676k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:SurvivorRatio=8 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T14:59:12.619+0800: 0.137: [GC (Allocation Failure) 2021-01-30T14:59:12.619+0800: 0.137: [DefNew: 5279K->815K(9216K), 0.0033567 secs] 5279K->4911K(60416K), 0.0034568 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 2021-01-30T14:59:12.623+0800: 0.141: [GC (Allocation Failure) 2021-01-30T14:59:12.623+0800: 0.141: [DefNew: 4911K->0K(9216K), 0.0010104 secs] 9007K->4906K(60416K), 0.0010550 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
 *
 * Heap
 *  def new generation   total 9216K, used 4337K [0x00000000fc400000, 0x00000000fce00000, 0x00000000fce00000)
 *   eden space 8192K,  52% used [0x00000000fc400000, 0x00000000fc83c448, 0x00000000fcc00000)
 *   from space 1024K,   0% used [0x00000000fcc00000, 0x00000000fcc00000, 0x00000000fcd00000)
 *   to   space 1024K,   0% used [0x00000000fcd00000, 0x00000000fcd00000, 0x00000000fce00000)
 *  tenured generation   total 51200K, used 4906K [0x00000000fce00000, 0x0000000100000000, 0x0000000100000000)
 *    the space 51200K,   9% used [0x00000000fce00000, 0x00000000fd2ca8a8, 0x00000000fd2caa00, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 126131, please wait...
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
 *    NewSize                  = 10485760 (10.0MB)
 *    MaxNewSize               = 10485760 (10.0MB)
 *    OldSize                  = 52428800 (50.0MB)
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
 *    capacity = 52428800 (50.0MB)
 *    used     = 5023912 (4.791175842285156MB)
 *    free     = 47404888 (45.208824157714844MB)
 *    9.582351684570312% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * 两个 4MB 对象一个在新生代，一个在老年代。0.5MB 的 b1 对象被存放在老年代，
 * 在垃圾回收的过程中，b1 对象的大小已经占据了 from 区的一半，故被直接送入了
 * 老年代。
 *
 * 示例二：
 *
 * 使用 `-XX:TargetSurvivorRatio` 参数（默认值为 50，表示 50%）提高 survior 区的利用率
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -Xmn10M -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden2
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6452952k free), swap 7929852k(1205720k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T15:48:34.710+0800: 0.115: [GC (Allocation Failure) 2021-01-30T15:48:34.710+0800: 0.115: [DefNew: 5279K->815K(9216K), 0.0046713 secs] 5279K->4911K(60416K), 0.0048096 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
 * 2021-01-30T15:48:34.715+0800: 0.120: [GC (Allocation Failure) 2021-01-30T15:48:34.715+0800: 0.120: [DefNew: 4911K->810K(9216K), 0.0013748 secs] 9007K->4906K(60416K), 0.0014388 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 9216K, used 5147K [0x00000000fc400000, 0x00000000fce00000, 0x00000000fce00000)
 *   eden space 8192K,  52% used [0x00000000fc400000, 0x00000000fc83c448, 0x00000000fcc00000)
 *   from space 1024K,  79% used [0x00000000fcc00000, 0x00000000fccca898, 0x00000000fcd00000)
 *   to   space 1024K,   0% used [0x00000000fcd00000, 0x00000000fcd00000, 0x00000000fce00000)
 *  tenured generation   total 51200K, used 4096K [0x00000000fce00000, 0x0000000100000000, 0x0000000100000000)
 *    the space 51200K,   8% used [0x00000000fce00000, 0x00000000fd200010, 0x00000000fd200200, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 128765, please wait...
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
 *    NewSize                  = 10485760 (10.0MB)
 *    MaxNewSize               = 10485760 (10.0MB)
 *    OldSize                  = 52428800 (50.0MB)
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
 *    used     = 5186496 (4.94622802734375MB)
 *    free     = 4250688 (4.05377197265625MB)
 *    54.958089192708336% used
 * Eden Space:
 *    capacity = 8388608 (8.0MB)
 *    used     = 4356904 (4.155067443847656MB)
 *    free     = 4031704 (3.8449325561523438MB)
 *    51.9383430480957% used
 * From Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 829592 (0.7911605834960938MB)
 *    free     = 218984 (0.20883941650390625MB)
 *    79.11605834960938% used
 * To Space:
 *    capacity = 1048576 (1.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 1048576 (1.0MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 52428800 (50.0MB)
 *    used     = 4194320 (4.0000152587890625MB)
 *    free     = 48234480 (45.99998474121094MB)
 *    8.000030517578125% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * 两个 4MB 对象一个在新生代，一个在老年代。0.5MB 的 b1 对象未超过 from 区的 90%，
 * 因此保存在新生代中。
 *
 * 示例三：
 *
 * 使用 `-XX:SurvivorRatio` 参数设置更大的 surivor 空间
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -Xmn10M -XX:SurvivorRatio=2 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden2
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6454016k free), swap 7929852k(1205840k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:MaxNewSize=10485760 -XX:NewSize=10485760 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:SurvivorRatio=2 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T15:50:31.623+0800: 0.109: [GC (Allocation Failure) 2021-01-30T15:50:31.623+0800: 0.109: [DefNew: 1143K->815K(7680K), 0.0018282 secs] 1143K->815K(58880K), 0.0019345 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
 * 2021-01-30T15:50:31.626+0800: 0.111: [GC (Allocation Failure) 2021-01-30T15:50:31.626+0800: 0.111: [DefNew: 4911K->810K(7680K), 0.0027650 secs] 4911K->4906K(58880K), 0.0028187 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 * 2021-01-30T15:50:31.629+0800: 0.115: [GC (Allocation Failure) 2021-01-30T15:50:31.629+0800: 0.115: [DefNew: 4906K->810K(7680K), 0.0009220 secs] 9002K->4906K(58880K), 0.0009620 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * Heap
 *  def new generation   total 7680K, used 5060K [0x00000000fc400000, 0x00000000fce00000, 0x00000000fce00000)
 *   eden space 5120K,  83% used [0x00000000fc400000, 0x00000000fc826800, 0x00000000fc900000)
 *   from space 2560K,  31% used [0x00000000fcb80000, 0x00000000fcc4a898, 0x00000000fce00000)
 *   to   space 2560K,   0% used [0x00000000fc900000, 0x00000000fc900000, 0x00000000fcb80000)
 *  tenured generation   total 51200K, used 4096K [0x00000000fce00000, 0x0000000100000000, 0x0000000100000000)
 *    the space 51200K,   8% used [0x00000000fce00000, 0x00000000fd200010, 0x00000000fd200200, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 128921, please wait...
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
 *    NewSize                  = 10485760 (10.0MB)
 *    MaxNewSize               = 10485760 (10.0MB)
 *    OldSize                  = 52428800 (50.0MB)
 *    NewRatio                 = 2
 *    SurvivorRatio            = 2
 *    MetaspaceSize            = 21807104 (20.796875MB)
 *    CompressedClassSpaceSize = 1073741824 (1024.0MB)
 *    MaxMetaspaceSize         = 17592186044415 MB
 *    G1HeapRegionSize         = 0 (0.0MB)
 *
 * Heap Usage:
 * New Generation (Eden + 1 Survivor Space):
 *    capacity = 7864320 (7.5MB)
 *    used     = 5128792 (4.891197204589844MB)
 *    free     = 2735528 (2.6088027954101562MB)
 *    65.21596272786458% used
 * Eden Space:
 *    capacity = 5242880 (5.0MB)
 *    used     = 4299200 (4.10003662109375MB)
 *    free     = 943680 (0.89996337890625MB)
 *    82.000732421875% used
 * From Space:
 *    capacity = 2621440 (2.5MB)
 *    used     = 829592 (0.7911605834960938MB)
 *    free     = 1791848 (1.7088394165039062MB)
 *    31.64642333984375% used
 * To Space:
 *    capacity = 2621440 (2.5MB)
 *    used     = 0 (0.0MB)
 *    free     = 2621440 (2.5MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 52428800 (50.0MB)
 *    used     = 4194320 (4.0000152587890625MB)
 *    free     = 48234480 (45.99998474121094MB)
 *    8.000030517578125% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * 两个 4MB 对象一个在新生代，一个在老年代。0.5MB 的 b1 对象未超过 from 区（2.5MB）的 一半，
 * 因此保存在新生代中。
 *
 */
public class PutInEden2 {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] b1, b2, b3;  // 定义变量
        b1 = new byte[1024 * 512];  // 分配 0.5MB 堆空间
        b2 = new byte[4 * _1MB];    // 分配 4MB 堆空间
        b3 = new byte[4 * _1MB];

        b3 = null;  // 使 b3 可以回收

        b3 = new byte[4 * _1MB];

        try {
            System.out.println("PutInEden2 is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}