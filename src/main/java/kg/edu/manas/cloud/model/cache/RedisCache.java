package kg.edu.manas.cloud.model.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCache {
    private final RedisTemplate<String, Object> redisTemplate;

    public Object get(String id) {
        return redisTemplate.opsForValue().get(id);
    }
    public void delete(String id) {
        redisTemplate.delete(id);
    }
    public void putWithTTL(String id, Object value) {
        redisTemplate.opsForValue().set(id, value, 20, TimeUnit.MINUTES);
    }
}
