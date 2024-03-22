package com.example.ratatouille23desktopclient.caching;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

import java.util.ArrayList;

public class RAT23Cache<K,T>{
    private static RAT23Cache<String, String> cacheInstance = null;

    private final long timeToLive;
    private final LRUMap cacheMap;

    protected class cacheObject {
        public long lastAccess = System.currentTimeMillis();
        public T value;

        protected cacheObject(T value) {
            this.value = value;
        }
    }

    private RAT23Cache(long cacheTimeToLive, final long timeInterval, int maxObjects){
        this.timeToLive = cacheTimeToLive * 1000;
        cacheMap = new LRUMap(maxObjects);
        if (timeToLive > 0 && timeInterval > 0) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timeInterval * 1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        cacheCleanUp();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    public static RAT23Cache getCacheInstance(){
        if (cacheInstance == null){
            cacheInstance = new RAT23Cache<>(1000,1000,10);
        }
        return cacheInstance;
    }

    public void put(K key, T value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new cacheObject(value));
        }
    }

    public T get(K key) {
        synchronized (cacheMap) {
            cacheObject c;
            c = (cacheObject) cacheMap.get(key);
            if (c == null)
                return null;
            else {
                c.lastAccess = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    public void cacheCleanUp(){
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (cacheMap) {
            MapIterator itr = cacheMap.mapIterator();

            deleteKey = new ArrayList<K>((cacheMap.size() / 2) + 1);
            K key = null;
            cacheObject c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (cacheObject) itr.getValue();
                if (c != null && (now > (timeToLive + c.lastAccess))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }
            Thread.yield();
        }
    }

}
