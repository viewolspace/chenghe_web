package com.chenghe.parttime.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lenovo on 2019/7/23.
 */
public class LruCache {

    HashMap<String,String> map = null;

    private int maxSize;

    public LruCache(int size){
        maxSize = size;
        map = new LinkedHashMap<String,String>((int) Math.ceil(maxSize / 0.75f) + 1, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized void put(String key,String value){
        map.put(key,value);
    }

    public synchronized void remove(String key){
        map.remove(key);
    }

    public String get(String key){
        return map.get(key);
    }
}
