package cn.tedu.concurrentmap;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentNavigableMapDemo {

    public static void main(String[] args) {

        // 构建映射对象
        ConcurrentNavigableMap<String, Integer> map =
                new ConcurrentSkipListMap<>();
        // 添加元素
        map.put("Peter", 19);
        map.put("Sandy", 17);
        map.put("David", 19);
        map.put("Helen", 18);
        map.put("Bruce", 21);
        map.put("Simon", 25);
        map.put("Jerry", 19);
        System.out.println(map);

        // 从头开始截取到指定的位置
        System.out.println(map.headMap("Helen"));
        // 从指定位置开始截取到尾部
        System.out.println(map.tailMap("Helen"));
        // 截取指定范围
        System.out.println(map.subMap("David", "Sandy"));
    }

}
