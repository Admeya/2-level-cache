package com.cache.decision_2.bli.cache_strategy;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Интерфейс для выбора стратегии кэширования
 */
public interface CacheStrategy {

    TreeMap<Object, Long> objectsStorage = new TreeMap<>();
    Map<Object, Long> sortedObjects = new TreeMap<>();

    /**
     * Добавить к ключу параметр, характеризующий ту или иную стратегию
     */
    default void writeKeyWithParameter(Object key) {
        objectsStorage.put(key, System.nanoTime());
    }

    /**
     * Применимость стратегии
     *
     * @param strategyName название стратегии
     * @return true, если указанная стратегия является выбранной для кэша
     */
    default boolean isApplicable(String strategyName) {
        return true;
    }

    /**
     * Объект который будет вытеснен из кэша из-за нехватки места
     *
     * @return объект
     */
    default Object getOldKey() {
        sortedObjects.putAll(objectsStorage);
        List<Map.Entry<Object, Long>> list = sortedObjects.entrySet().stream()
                .sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()))
                .collect(Collectors.toList());
        int listSize = list.size() - 1;
        return list.get(listSize).getKey();
    }

    /**
     * Удаление ключа из мапы стратегий
     *
     * @param key ключ объекта
     */
    default void removeFromStrategy(Object key) {
        objectsStorage.remove(key);
    }
}
