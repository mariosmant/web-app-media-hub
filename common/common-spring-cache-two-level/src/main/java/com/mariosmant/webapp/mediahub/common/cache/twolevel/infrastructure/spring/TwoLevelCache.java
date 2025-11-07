package com.mariosmant.webapp.mediahub.common.cache.twolevel.infrastructure.spring;

import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

final class TwoLevelCache implements Cache {

    private final Cache l1Cache;
    private final Cache l2Cache;
    private final String name;

    public TwoLevelCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    @Override
    public ValueWrapper get(@NonNull Object key) {
        ValueWrapper value = l1Cache.get(key);
        if (value != null) return value;

        value = l2Cache.get(key);
        if (value != null) {
            l1Cache.put(key, value.get()); // backfill L1
        }
        return value;
    }

    @Override
    public <T> T get(@NonNull Object key, Class<T> type) {
        T value = l1Cache.get(key, type);
        if (value != null) return value;

        value = l2Cache.get(key, type);
        if (value != null) {
            l1Cache.put(key, value);
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        // Try L1
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }

        // Try L2
        wrapper = l2Cache.get(key);
        if (wrapper != null) {
            T value = (T) wrapper.get();
            l1Cache.put(key, value); // backfill L1
            return value;
        }

        // Load if absent
        try {
            T value = valueLoader.call();
            if (value != null) {
                l1Cache.put(key, value);
                l2Cache.put(key, value);
            }
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public CompletableFuture<?> retrieve(@NonNull Object key) {
        // Try L1
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            return CompletableFuture.completedFuture(wrapper.get());
        }

        // Try L2
        wrapper = l2Cache.get(key);
        if (wrapper != null) {
            Object value = wrapper.get();
            l1Cache.put(key, value);
            return CompletableFuture.completedFuture(value);
        }

        // Nothing found
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull <T> CompletableFuture<T> retrieve(
            @NonNull Object key,
            @NonNull Supplier<CompletableFuture<T>> valueLoader) {

        // Try L1
        T value = l1Cache.get(key, (Class<T>) Object.class);
        if (value != null) {
            return CompletableFuture.completedFuture(value);
        }

        // Try L2
        value = l2Cache.get(key, (Class<T>) Object.class);
        if (value != null) {
            l1Cache.put(key, value);
            return CompletableFuture.completedFuture(value);
        }

        // Load asynchronously
        return valueLoader.get().thenApply(v -> {
            if (v != null) {
                l1Cache.put(key, v);
                l2Cache.put(key, v);
            }
            return v;
        });
    }

    @Override
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull Object getNativeCache() {
        // You could return both, but usually return the "primary" one
        return l2Cache.getNativeCache();
    }

    @Override
    public void put(@NonNull Object key, Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        ValueWrapper wrapper = l1Cache.putIfAbsent(key, value);
        l2Cache.putIfAbsent(key, value);
        return wrapper;
    }

    @Override
    public void evict(@NonNull Object key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public boolean evictIfPresent(@NonNull Object key) {
        boolean removedL1 = l1Cache.evictIfPresent(key);
        boolean removedL2 = l2Cache.evictIfPresent(key);
        return removedL1 || removedL2;
    }

    @Override
    public void clear() {
        l1Cache.clear();
        l2Cache.clear();
    }

    @Override
    public boolean invalidate() {
        boolean invalidatedL1 = l1Cache.invalidate();
        boolean invalidatedL2 = l2Cache.invalidate();
        return invalidatedL1 || invalidatedL2;
    }
}
