package com.cache.decision_2.bl.cache_strategy;

/**
 * Типы стратегий при вытеснении объектов из кэша
 */
public enum StrategyTypes {
    LFU, // Least frequently used - вытеснение наименее часто используемых
    LRU, // Least recently used - вытеснение давно не используемых
    FIFO // First in First out вытеснение первого попавшего в кэш объекта
}
