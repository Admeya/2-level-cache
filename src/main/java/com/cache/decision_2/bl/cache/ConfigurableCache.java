package com.cache.decision_2.bl.cache;

import com.cache.decision_2.bl.cache_strategy.FIFOStrategy;
import com.cache.decision_2.bl.cache_strategy.LFUStrategy;
import com.cache.decision_2.bl.cache_strategy.LRUStrategy;
import com.cache.decision_2.bl.cache_strategy.StrategyFactory;
import com.cache.decision_2.bli.cache.Cache;
import com.cache.decision_2.bli.cache_strategy.CacheStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.cache.decision_2.bl.cache_strategy.StrategyTypes.LRU;

/**
 * Двухуровневый кэш для кэширования объектов
 * первый уровень - память
 * второй уровень - файловая система
 */
@Slf4j
public class ConfigurableCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private static int COUNT_OBJECTS_IN_MEMORY = 0; // неограниченное число объектов
    private static int COUNT_OBJECTS_IN_FILE = 0; // неограниченное число объектов
    private static String CACHE_ALGORITHM_MEMORY = LRU.name();
    private static String CACHE_ALGORITHM_FILE = LRU.name();

    private final MemoryCache<K, V> memoryCache;
    private final FileCache<K, V> fileCache;

    private final StrategyFactory factory;
    private final CacheStrategy memoryStrategy;
    private final CacheStrategy fileStrategy;

    static {
        try {
            Properties cacheProperties = new Properties();
            cacheProperties.load(new FileInputStream(new File("src/main/resources/decision_2/ini.config")));
            COUNT_OBJECTS_IN_MEMORY = Integer.valueOf(cacheProperties.getProperty("COUNT_OBJECTS_IN_MEMORY"));
            COUNT_OBJECTS_IN_FILE = Integer.valueOf(cacheProperties.getProperty("COUNT_OBJECTS_IN_FILE"));
            CACHE_ALGORITHM_MEMORY = cacheProperties.getProperty("CACHE_ALGORITHM_MEMORY");
            CACHE_ALGORITHM_FILE = cacheProperties.getProperty("CACHE_ALGORITHM_FILE");
        } catch (IOException e) {
            log.error("Can't load properties");
        }
    }

    {
        factory = new StrategyFactory(initStrategyList());
    }

    ConfigurableCache() {
        memoryCache = new MemoryCache(COUNT_OBJECTS_IN_MEMORY);
        fileCache = new FileCache(COUNT_OBJECTS_IN_FILE);
        memoryStrategy = getStrategyForMemory();
        fileStrategy = getStrategyForFile();
    }

    ConfigurableCache(MemoryCache memCache, FileCache fileCache, CacheStrategy memoryStrategy, CacheStrategy fileStrategy) {
        this.memoryCache = memCache;
        this.fileCache = fileCache;
        this.memoryStrategy = memoryStrategy;
        this.fileStrategy = fileStrategy;
    }

    private List<CacheStrategy> initStrategyList() {
        List<CacheStrategy> strategyList = new ArrayList<>();
        strategyList.add(new FIFOStrategy());
        strategyList.add(new LFUStrategy());
        strategyList.add(new LRUStrategy());
        return strategyList;
    }

    private CacheStrategy getStrategyForMemory() {
        return factory.getStrategy(CACHE_ALGORITHM_MEMORY);
    }

    private CacheStrategy getStrategyForFile() {
        return factory.getStrategy(CACHE_ALGORITHM_FILE);
    }

    @Override
    public void putToCache(K key, V object) {
        if (memoryCache.isKeyInCache(key) || memoryCache.isCacheEnableToPut()) {
            log.debug(String.format("Put object with key %s to the memory cache", key));
            memoryCache.putToCache(key, object);
            memoryStrategy.writeKeyWithParameter(key);
            if (fileCache.isKeyInCache(key)) {
                fileCache.removeFromCache(key);
            }
        } else if (fileCache.isKeyInCache(key) || fileCache.isCacheEnableToPut()) {
            log.debug(String.format("Put object with key %s to the file cache", key));
            fileCache.putToCache(key, object);
            fileStrategy.writeKeyWithParameter(key);
        } else {
            outageCache(key, object);
        }
    }

    void outageCache(K key, V value) {
        K replacedKeyInMemory = (K) memoryStrategy.getOldKey();
        K replacedKeyInFiles = (K) fileStrategy.getOldKey();

        if (memoryCache.isKeyInCache(replacedKeyInMemory)) {
            log.debug(String.format("Replace object with key %s from memory", replacedKeyInMemory));
            memoryCache.removeFromCache(replacedKeyInMemory);
            memoryCache.putToCache(key, value);
        } else if (fileCache.isKeyInCache(replacedKeyInFiles)) {
            log.debug(String.format("Replace object with key %s from file", replacedKeyInFiles));
            fileCache.removeFromCache(replacedKeyInFiles);
            fileCache.putToCache(key, value);
        }
    }

    @Override
    public V getObject(K key) {
        V value = null;
        if (memoryCache.isKeyInCache(key)) {
            memoryStrategy.writeKeyWithParameter(key);
            value = memoryCache.getObject(key);
        } else if (fileCache.isKeyInCache(key)) {
            fileStrategy.writeKeyWithParameter(key);
            value = fileCache.getObject(key);
        }
        return value;
    }

    @Override
    public void removeFromCache(K key) {
        if (memoryCache.isKeyInCache(key)) {
            log.debug(String.format("Remove object with key %s from memory", key));
            memoryCache.removeFromCache(key);
            memoryStrategy.removeFromStrategy(key);
        }
        if (fileCache.isKeyInCache(key)) {
            log.debug(String.format("Remove object with key %s from memory", key));
            fileCache.removeFromCache(key);
            fileStrategy.removeFromStrategy(key);
        }
    }

    @Override
    public boolean isKeyInCache(K key) {
        return memoryCache.isKeyInCache(key) || fileCache.isKeyInCache(key);
    }

    @Override
    public boolean isCacheEnableToPut() {
        return memoryCache.isCacheEnableToPut() || fileCache.isCacheEnableToPut();
    }

    @Override
    public int cacheSize() {
        return memoryCache.cacheSize() + fileCache.cacheSize();
    }

    @Override
    public void clearCache() {
        memoryCache.clearCache();
        fileCache.clearCache();
    }
}
