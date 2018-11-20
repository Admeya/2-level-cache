package com.cache.decision_2.bl.cache;

import com.cache.decision_2.bli.cache.Cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация первого уровня кэширования - в памяти
 */
public class MemoryCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private Map<K, V> cacheObjects = new HashMap<>();
    private final int countObjects;

    MemoryCache(int countObjects) {
        this.countObjects = countObjects;
    }

    @Override
    public V getObject(K key) {
        return cacheObjects.get(key);
    }

    @Override
    public void putToCache(K key, V object) {
        cacheObjects.put(key, object);
    }

    @Override
    public void removeFromCache(K key) {
        cacheObjects.remove(key);
    }

    @Override
    public boolean isKeyInCache(K key) {
        return cacheObjects.containsKey(key);
    }

    @Override
    public boolean isCacheEnableToPut() {
        return cacheObjects.size() < countObjects;
    }

    @Override
    public int cacheSize() {
        return cacheObjects.size();
    }

    @Override
    public void clearCache() {
        cacheObjects.clear();
    }
}
