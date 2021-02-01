package com.example.jvm.demo.performance;

import java.io.IOException;

/**
 * 将新对象预留在新生代
 *
 * 示例一：
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -Xmn6M -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6461484k free), swap 7929852k(1201492k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:MaxNewSize=6291456 -XX:NewSize=6291456 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * 2021-01-30T14:08:54.841+0800: 0.111: [GC (Allocation Failure) 2021-01-30T14:08:54.841+0800: 0.111: [DefNew: 4731K->303K(5568K), 0.0028289 secs] 4731K->4399K(60864K), 0.0029275 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
 * 2021-01-30T14:08:54.844+0800: 0.115: [GC (Allocation Failure) 2021-01-30T14:08:54.844+0800: 0.115: [DefNew: 4399K->0K(5568K), 0.0022167 secs] 8495K->8490K(60864K), 0.0022657 secs] [Times: user=0.00 sys=0.01, real=0.00 secs]
 * 2021-01-30T14:08:54.847+0800: 0.117: [GC (Allocation Failure) 2021-01-30T14:08:54.847+0800: 0.117: [DefNew: 4096K->0K(5568K), 0.0016308 secs] 12586K->12586K(60864K), 0.0016651 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 *
 * Heap
 *  def new generation   total 5568K, used 4243K [0x00000000fc400000, 0x00000000fca00000, 0x00000000fca00000)
 *   eden space 4992K,  85% used [0x00000000fc400000, 0x00000000fc824ff0, 0x00000000fc8e0000)
 *   from space 576K,   0% used [0x00000000fc970000, 0x00000000fc970000, 0x00000000fca00000)
 *   to   space 576K,   0% used [0x00000000fc8e0000, 0x00000000fc8e0000, 0x00000000fc970000)
 *  tenured generation   total 55296K, used 12586K [0x00000000fca00000, 0x0000000100000000, 0x0000000100000000)
 *    the space 55296K,  22% used [0x00000000fca00000, 0x00000000fd64a8b8, 0x00000000fd64aa00, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 123406, please wait...
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
 *    NewSize                  = 6291456 (6.0MB)
 *    MaxNewSize               = 6291456 (6.0MB)
 *    OldSize                  = 56623104 (54.0MB)
 *    NewRatio                 = 2
 *    SurvivorRatio            = 8
 *    MetaspaceSize            = 21807104 (20.796875MB)
 *    CompressedClassSpaceSize = 1073741824 (1024.0MB)
 *    MaxMetaspaceSize         = 17592186044415 MB
 *    G1HeapRegionSize         = 0 (0.0MB)
 *
 * Heap Usage:
 * New Generation (Eden + 1 Survivor Space):
 *    capacity = 5701632 (5.4375MB)
 *    used     = 4294352 (4.0954132080078125MB)
 *    free     = 1407280 (1.3420867919921875MB)
 *    75.31794405531609% used
 * Eden Space:
 *    capacity = 5111808 (4.875MB)
 *    used     = 4294352 (4.0954132080078125MB)
 *    free     = 817456 (0.7795867919921875MB)
 *    84.00847606169872% used
 * From Space:
 *    capacity = 589824 (0.5625MB)
 *    used     = 0 (0.0MB)
 *    free     = 589824 (0.5625MB)
 *    0.0% used
 * To Space:
 *    capacity = 589824 (0.5625MB)
 *    used     = 0 (0.0MB)
 *    free     = 589824 (0.5625MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 56623104 (54.0MB)
 *    used     = 12888248 (12.291191101074219MB)
 *    free     = 43734856 (41.70880889892578MB)
 *    22.761465001989293% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * b1, b2, b3, b4 的大小都是 4MB，且都是新对象，但是 eden 空间 5MB 多，仅仅可以存放一个数组对象，
 * 因此，在经过连续 4 次空间分配后，只有 b4 可以在 eden 区，而其他 3 个数组
 * 对象都已经移动到了老年代。
 *
 * 示例二：
 *
 * 使用 `-Xmn`  或者 `-XX:NewRatio` 参数分配足够大的新生代空间
 *
 * 启动命令：
 *     java -Xms60M -Xmx60M -Xmn30M -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden
 *     java -Xms60M -Xmx60M -XX:NewRatio=1 -XX:+UseSerialGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc-%t.log -cp jvm-demo-0.0.1-SNAPSHOT.jar com.example.jvm.demo.performance.PutInEden
 *
 * GC 日志内容：
 * Java HotSpot(TM) 64-Bit Server VM (25.131-b11) for linux-amd64 JRE (1.8.0_131-b11), built on Mar 15 2017 01:23:40 by "java_re" with gcc 4.3.0 20080428 (Red Hat 4.3.0-8)
 * Memory: 4k page, physical 15673600k(6460844k free), swap 7929852k(1201648k free)
 * CommandLine flags: -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=62914560 -XX:NewRatio=1 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
 * Heap
 *  def new generation   total 27648K, used 17859K [0x00000000fc400000, 0x00000000fe200000, 0x00000000fe200000)
 *   eden space 24576K,  72% used [0x00000000fc400000, 0x00000000fd570c10, 0x00000000fdc00000)
 *   from space 3072K,   0% used [0x00000000fdc00000, 0x00000000fdc00000, 0x00000000fdf00000)
 *   to   space 3072K,   0% used [0x00000000fdf00000, 0x00000000fdf00000, 0x00000000fe200000)
 *  tenured generation   total 30720K, used 0K [0x00000000fe200000, 0x0000000100000000, 0x0000000100000000)
 *    the space 30720K,   0% used [0x00000000fe200000, 0x00000000fe200000, 0x00000000fe200200, 0x0000000100000000)
 *  Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
 *   class space    used 320K, capacity 386K, committed 512K, reserved 1048576K
 *
 * 堆信息：
 * Attaching to process ID 123583, please wait...
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
 *    NewSize                  = 31457280 (30.0MB)
 *    MaxNewSize               = 31457280 (30.0MB)
 *    OldSize                  = 31457280 (30.0MB)
 *    NewRatio                 = 1
 *    SurvivorRatio            = 8
 *    MetaspaceSize            = 21807104 (20.796875MB)
 *    CompressedClassSpaceSize = 1073741824 (1024.0MB)
 *    MaxMetaspaceSize         = 17592186044415 MB
 *    G1HeapRegionSize         = 0 (0.0MB)
 *
 * Heap Usage:
 * New Generation (Eden + 1 Survivor Space):
 *    capacity = 28311552 (27.0MB)
 *    used     = 17783944 (16.96009063720703MB)
 *    free     = 10527608 (10.039909362792969MB)
 *    62.81515050817419% used
 * Eden Space:
 *    capacity = 25165824 (24.0MB)
 *    used     = 17783944 (16.96009063720703MB)
 *    free     = 7381880 (7.039909362792969MB)
 *    70.66704432169597% used
 * From Space:
 *    capacity = 3145728 (3.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 3145728 (3.0MB)
 *    0.0% used
 * To Space:
 *    capacity = 3145728 (3.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 3145728 (3.0MB)
 *    0.0% used
 * tenured generation:
 *    capacity = 31457280 (30.0MB)
 *    used     = 0 (0.0MB)
 *    free     = 31457280 (30.0MB)
 *    0.0% used
 *
 * 803 interned Strings occupying 54232 bytes.
 *
 * 分析：
 * b1, b2, b3, b4 的大小都是 4MB，且都是新对象，eden 空间有 24MB，足以容纳这 4 个数组对象
 *
 */
public class PutInEden {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] b1, b2, b3, b4;  // 定义变量
        b1 = new byte[4 * _1MB];  // 分配4MB堆空间，考察堆空间的使用情况
        b2 = new byte[4 * _1MB];
        b3 = new byte[4 * _1MB];
        b4 = new byte[4 * _1MB];

        try {
            System.out.println("PutInEden is started.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}