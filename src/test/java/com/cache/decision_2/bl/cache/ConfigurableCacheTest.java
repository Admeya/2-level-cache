package com.cache.decision_2.bl.cache;

import com.cache.decision_2.bl.cache_strategy.LFUStrategy;
import com.cache.decision_2.bl.cache_strategy.LRUStrategy;
import com.cache.decision_2.bli.cache_strategy.CacheStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class ConfigurableCacheTest {

    private ConfigurableCache<String, String> cache = new ConfigurableCache<>();
    private MemoryCache memoryCache;
    private FileCache fileCache;
    private ConfigurableCache<String, String> newCache;

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    @Before
    public void init() {
        memoryCache = new MemoryCache(1);
        fileCache = new FileCache(2);

        CacheStrategy memoryStrategy = new LFUStrategy();
        CacheStrategy fileStrategy = new LRUStrategy();

        newCache = new ConfigurableCache<>(memoryCache, fileCache, memoryStrategy, fileStrategy);
    }

    @Test
    public void putToCacheTest() {
        String keyOne = getUUID();
        String keyTwo = getUUID();

        cache.putToCache(keyOne, "for memory");
        cache.putToCache(keyTwo, "for memory");

        Assert.assertTrue(cache.isKeyInCache(keyOne));
        Assert.assertTrue(cache.isKeyInCache(keyTwo));
        Assert.assertEquals(2, cache.cacheSize());
    }

    @Test
    public void outageCacheTest() {
        cache.clearCache();
        for (int i = 0; i < 11; i++) {
            cache.putToCache(getUUID(), "for memory");
        }
        Assert.assertEquals(10, cache.cacheSize()-1);
        String newKey = getUUID();
        cache.outageCache(newKey, "value1");
        Assert.assertEquals(10, cache.cacheSize()-1);
        Assert.assertTrue( cache.isKeyInCache(newKey));
    }

    @Test
    public void cacheSizeTest() {
        memoryCache.putToCache(getUUID(), "1");
        fileCache.putToCache(getUUID(), "2");
        fileCache.putToCache(getUUID(), "3");

        Assert.assertEquals(3, newCache.cacheSize());
    }

    @Test
    public void isCacheEnableToPutTest() {
        Assert.assertTrue(newCache.isCacheEnableToPut());

        memoryCache.putToCache(getUUID(), "1");
        fileCache.putToCache(getUUID(), "2");
        fileCache.putToCache(getUUID(), "3");

        Assert.assertFalse(newCache.isCacheEnableToPut());
    }

    @Test
    public void isKeyInCacheTest() {
        String key = getUUID();
        memoryCache.putToCache(key, "1");
        Assert.assertTrue(newCache.isKeyInCache(key));
    }

    @Test
    public void clearCacheTest(){
        cache.clearCache();
        Assert.assertEquals( 0, cache.cacheSize());
    }

    @Test
    public void getObjectTest(){
        newCache.clearCache();
        String key1 = getUUID();
        String key2 = getUUID();

        memoryCache.putToCache(key1, "1");
        fileCache.putToCache(key2, "2");

        Assert.assertEquals("2", newCache.getObject(key2));
    }
}
