package com.cache.decision_2.bl.cache_strategy;

import com.cache.decision_2.bli.cache_strategy.CacheStrategy;

import static com.cache.decision_2.bl.cache_strategy.StrategyTypes.FIFO;

/**
 * Стратегия вытеснения объектов первый вошел - первый вышел
 */
public class FIFOStrategy implements CacheStrategy {

  @Override
  public boolean isApplicable(String strategyName) {
    return FIFO.name().equals(strategyName);
  }

  @Override
  public Object getOldKey() {
    return objectsStorage.firstKey();
  }
}
