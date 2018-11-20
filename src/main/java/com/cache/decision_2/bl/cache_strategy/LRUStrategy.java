package com.cache.decision_2.bl.cache_strategy;

import com.cache.decision_2.bli.cache_strategy.CacheStrategy;

import static com.cache.decision_2.bl.cache_strategy.StrategyTypes.LRU;

/**
 * Стратегия вытеснения объектов по времени создания
 */
public class LRUStrategy implements CacheStrategy {

    @Override
    public boolean isApplicable(String strategyName) {
        return LRU.name().equals(strategyName);
    }
}
