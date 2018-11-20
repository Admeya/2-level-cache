package com.cache.decision_2.bl.cache_strategy;

import com.cache.decision_2.bli.cache_strategy.CacheStrategy;

import static com.cache.decision_2.bl.cache_strategy.StrategyTypes.LFU;

/**
 * Стратегия вытеснения объектов по частоте использования
 */
public class LFUStrategy implements CacheStrategy {

  @Override
  public void writeKeyWithParameter(Object key) {
    long frequence = 1;
    if (objectsStorage.containsKey(key)) {
      frequence = objectsStorage.get(key) + 1;
    }
    objectsStorage.put(key, frequence);
  }

  @Override
  public boolean isApplicable(String strategyName) {
    return LFU.name().equals(strategyName);
  }
}
