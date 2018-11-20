package com.cache.decision_2.bl.cache_strategy;

import com.cache.decision_2.bli.cache_strategy.CacheStrategy;

import java.util.List;

/**
 * Фабрика стратегий
 */
public class StrategyFactory {
    private List<CacheStrategy> providers;

    public StrategyFactory(List<CacheStrategy> providers) {
        this.providers = providers;
    }

    /**
     * Выбор провайдера по наименованию стратегии
     *
     * @param strategyName наименование стратегии
     */
    public CacheStrategy getStrategy(String strategyName) {
        CacheStrategy result = new LRUStrategy();
        for (CacheStrategy provider : providers) {
            if (provider.isApplicable(strategyName)) {
                result = provider;
            }
        }
        return result;
    }
}
