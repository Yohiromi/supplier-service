package com.example.supplierservice;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSet() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("name", "kai");
    }
    @Test
    public void testGet() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String res = ops.get("supplier:risk:28");

    System.out.println(res);}
    @Test
    public void clearAll() {
        var keys = stringRedisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            System.out.println("Deleted all keys: " + keys.size());
        } else {
            System.out.println("No keys found in Redis.");
        }
    }

}

