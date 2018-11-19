package com.cache.decision_1;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Класс для примера работы с библиотекой ehcache
 *
 * Параметры для кэша первого уровня и второго уровня настраиваемы и находятся в ehcache.xml
 */
public class CacheExample {
    public static final int COUNT_ELEMENTS = 3;

    /**
     * Точка входа в приложение
     *
     * @param args аргументы консольного приложения
     */
    public static void main(String[] args) {
        CacheManager cm = CacheManager.newInstance("src/main/resources/decision_1/ehcache.xml");
        Cache l1Memory = cm.getCache("L1_memory");
        Cache l2File = cm.getCache("L2_file");

        printCacheInfo(l1Memory);
        printCacheInfo(l2File);

        cm.shutdown();
    }

    /**
     * Тестирование параметров кэша
     *
     * @param cache кэш
     */
    static void printCacheInfo(Cache cache) {
        Map<Integer, String> uuids = new HashMap<Integer, String>();
        String cacheName = cache.getName();
        initialTestObjects(uuids, cache, COUNT_ELEMENTS, cacheName);

        int elementNumber = getRandomNumber(COUNT_ELEMENTS);
        String elementKey = uuids.get(elementNumber);
        Element ele = cache.get(elementKey);

        System.out.println(cacheName + ". Is " + elementNumber + " in cache? (" + elementKey + ") " + cache.isKeyInCache(elementKey));
        System.out.println("Element value is: " + ((ele == null) ? null : ele.getObjectValue().toString()));
        System.out.println(cacheName + " size: " + cache.getSize());
        System.out.println(cacheName + " strategy: " + cache.getMemoryStoreEvictionPolicy().getName());
        System.out.println();
    }

    /**
     * @return уникальный идентификатор
     */
    static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Заполнение мапы объектов тестовыми данными и сохранение объектов в кэш
     *
     * @param uuids         мапа для соответствия объекта (guid -> значение)
     * @param cache         используемый кэш
     * @param countElements число элементов, помещаемых в кэш
     */
    static void initialTestObjects(Map<Integer, String> uuids, Cache cache, int countElements, String cacheName) {
        System.out.println("Generating elements for " + cacheName);
        for (int i = 0; i < countElements; i++) {
            String guid = getUUID();
            uuids.put(i, guid);
            cache.put(new Element(guid, "Element for saving " + i));
            System.out.println("element " + i + " = " + guid);
        }
    }

    /**
     * @return произвольный номер из диапазона
     */
    static int getRandomNumber(int range) {
        Random randomElement = new Random();
        return randomElement.nextInt(range);
    }
}
