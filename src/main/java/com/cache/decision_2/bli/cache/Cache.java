package com.cache.decision_2.bli.cache;

/**
 * Интерфейс, для реализации основных действий с кэшем
 */
public interface Cache<K, V> {

    /**
     * Получить объект из кэша по ключу
     *
     * @return ранее сохраненный в кэше объект
     */
    V getObject(K key);

    /**
     * Помещение объекта в кэш
     *
     * @param key    ключ объекта в кэше
     * @param object объект для кэширования
     */
    void putToCache(K key, V object);

    /**
     * Удаление из кэша
     *
     * @param key ключ объекта в кэше
     */
    void removeFromCache(K key);

    /**
     * Проверка, содержится ли в кэше данный объект
     *
     * @param key ключ объекта
     * @return true - объект уже есть в кэше
     */
    boolean isKeyInCache(K key);

    /**
     * Проверка, доступно ли место для записи объектов в кэш
     *
     * @return true - еще есть место для помещения объекта
     *         false - кэш заполнен, необходимо вытеснять объект, чтобы поместить новый
     */
    boolean isCacheEnableToPut();

    /**
     * Получить размер кэша
     *
     * @return размер кэша
     */
    int cacheSize();

    /**
     * Очистка кэша
     */
    void clearCache();
}
