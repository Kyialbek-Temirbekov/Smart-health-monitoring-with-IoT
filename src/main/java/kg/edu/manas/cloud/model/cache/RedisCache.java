package kg.edu.manas.cloud.model.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCache {
    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<Object> get(String id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(id));
    }
    public void putWithTTL(String id, Object value, long timeout) {
        redisTemplate.opsForValue().set(id, value, timeout, TimeUnit.MINUTES);
    }
    public long getExpire(String id) {
        return redisTemplate.getExpire(id, TimeUnit.MINUTES);
    }
    public void remove(String id) {
        redisTemplate.delete(id);
    }
}
