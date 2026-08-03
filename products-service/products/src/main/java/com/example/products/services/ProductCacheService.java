package com.example.products.services;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.products.models.Product;

@Service
public class ProductCacheService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private final RedisTemplate<String, Object> redisTemplate;

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
            return (Product) o;
        }
        return null;
    }

    public void putProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("Product and Product ID must not be null");
        }

        String cacheKey = key(product.getId());
        redisTemplate.opsForValue().set(cacheKey, product, DEFAULT_TTL.getSeconds(), TimeUnit.SECONDS);
    }

    public void evictProduct(UUID id) {
        redisTemplate.delete(key(id));
    }
}
