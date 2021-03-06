package net.arver.miaosha.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis服务
 */
@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 后置处理.
     */
    @PostConstruct
    public void init() {
        final Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        final RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

    }

    /**
     * 根据key查询
     * @param keyPrefix key前缀
     * @param key key
     * @param clazz 类型
     * @param <T> 泛型
     * @return 缓存
     */
    public <T> T get(final KeyPrefix keyPrefix, final String key, final Class<T> clazz) {
        final String realKey = genRealKey(keyPrefix, key);
        return (T) redisTemplate.opsForValue().get(realKey);
    }

    /**
     * 存入缓存
     * @param keyPrefix key前缀
     * @param key key
     * @param value 缓存值
     */
    public void set(final KeyPrefix keyPrefix, final String key, final Object value) {
        final String realKey = genRealKey(keyPrefix, key);
        final int seconds = keyPrefix.expireSeconds();
        if (seconds <= 0) {
            redisTemplate.opsForValue().set(realKey, value);
        } else {
            redisTemplate.opsForValue().set(realKey, value, seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 删除缓存.
     * @param keyPrefix key前缀
     * @param key key
     * @return 删除结果
     */
    public boolean delete(final KeyPrefix keyPrefix, final String key) {
        final String realKey = genRealKey(keyPrefix, key);
        return redisTemplate.delete(realKey);
    }

    /**
     * 删除指定key前缀的所有索引.
     * @param keyPrefix key前缀
     * @return 是否删除成功
     */
    public boolean delete(final KeyPrefix keyPrefix) {
        if (keyPrefix == null) {
            return false;
        }
        final Set keys = redisTemplate.keys(keyPrefix.getPrefix() + "*");
        redisTemplate.delete(keys);
        return true;
    }

    /**
     * 判断key是否存在
     * @param keyPrefix key前缀
     * @param key key
     * @return 是否存在
     */
    public boolean exist(final KeyPrefix keyPrefix, final String key) {
        final String realKey = genRealKey(keyPrefix, key);
        return redisTemplate.hasKey(realKey);
    }

    /**
     * 加1操作
     * @param keyPrefix key前缀
     * @param key key
     * @return 运算结果
     */
    public Long increment(final KeyPrefix keyPrefix, final String key) {
        final String realKey = genRealKey(keyPrefix, key);
        return redisTemplate.opsForValue().increment(realKey);
    }

    /**
     * 减1操作
     * @param keyPrefix key前缀
     * @param key key
     * @return 运算结果
     */
    public Long decrement(final KeyPrefix keyPrefix, final String key) {
        final String realKey = genRealKey(keyPrefix, key);
        return redisTemplate.opsForValue().decrement(realKey);
    }

    /**
     * 生成rediskey.
     * @param keyPrefix 前缀
     * @param key key
     * @return real key
     */
    private String genRealKey(final KeyPrefix keyPrefix, final String key) {
        return keyPrefix.getPrefix() + ":" + key;
    }

}
