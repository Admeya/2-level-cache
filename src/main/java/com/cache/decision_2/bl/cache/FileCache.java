package com.cache.decision_2.bl.cache;

import com.cache.decision_2.bli.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация второго уровня кэширования - на диске
 */
@Slf4j
public class FileCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private Map<K, V> cacheObjects = new HashMap<>();
    private final int countObjects;
    private Path path;

    {
        try {
            path = Files.createTempDirectory("temp");
        } catch (IOException e) {
            log.error("Error in create temp directory ", e);
        }
    }


    FileCache(int countObjects) {
        this.countObjects = countObjects;
    }

    @Override
    public V getObject(K key) {
        V objectValue = null;
        if (isKeyInCache(key)) {
            String fileName = (String) cacheObjects.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(new File(path + File.separator + fileName));
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                objectValue = (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("Not possible to read file ", e);
            }
        }
        return objectValue;
    }

    @Override
    public void putToCache(K key, V object) {
        File tempFile = null;
        try {
            tempFile = Files.createTempFile(path, "", "").toFile();
        } catch (IOException e) {
            log.error("Error in create temp directory ", e);
        }
        if (tempFile == null) return;
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            outputStream.writeObject(object);
            outputStream.flush();
            cacheObjects.put(key, (V) tempFile.getName());
        } catch (IOException e) {
            log.error("Exception in writing object on Disk " + tempFile.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void removeFromCache(K key) {
        String fileName = (String) cacheObjects.get(key);
        File deletedFile = new File(path + File.separator + fileName);
        if (deletedFile.delete()) cacheObjects.remove(key);
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
