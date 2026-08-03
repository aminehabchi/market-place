package com.example.products.services;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.products.models.Product;

@Service
public class ProductCacheService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(ProductCacheService.class);

    public ProductCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(UUID id) {
        return "product:" + id.toString();
    }

    public Product getCachedProduct(UUID id) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object o = ops.get(key(id));
        if (o instanceof Product) {
            if (log.isTraceEnabled()) log.trace("Cache hit for product {}", id);
            return (Product) o;
        }
        if (log.isTraceEnabled()) log.trace("Cache miss for product {}", id);
        return null;
    }

    public void putProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product and Product ID must not be null");
        }

        String cacheKey = key(product.getId());
        try {
            if (log.isDebugEnabled()) log.debug("Setting cache key {} for product {} (ttl {}s)", cacheKey, product.getId(), DEFAULT_TTL.getSeconds());
            redisTemplate.opsForValue().set(cacheKey, product, DEFAULT_TTL.getSeconds(), TimeUnit.SECONDS);
            if (log.isDebugEnabled()) log.debug("Cache set succeeded for key {}", cacheKey);
        } catch (Exception e) {
            log.error("Failed to set product cache key {} for product {}", cacheKey, product.getId(), e);
            // do not rethrow - cache failures should not break primary flow
        }
    }

    public void evictProduct(UUID id) {
        try {
            String k = key(id);
            if (log.isDebugEnabled()) log.debug("Evicting cache key {}", k);
            redisTemplate.delete(k);
        } catch (Exception e) {
            log.error("Failed to evict cache for product {}", id, e);
        }
    }
}
