package com.yusheng.hbgj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * redis配置<br>
 * 集群下启动session共享，需打开@EnableRedisHttpSession<br>
 * 单机下不需要
 *
 * @author Jinwei
 * <p>
 * 2017年8月10日
 */
//@EnableRedisHttpSession
@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    /* @Bean
     public RedisTemplate<String, Object> stringSerializerRedisTemplate() {
         RedisSerializer<String> stringSerializer = new StringRedisSerializer();
         redisTemplate.setKeySerializer(stringSerializer);
         redisTemplate.setValueSerializer(stringSerializer);
         redisTemplate.setHashKeySerializer(stringSerializer);
         redisTemplate.setHashValueSerializer(stringSerializer);
         return redisTemplate;
     }*/
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);


        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        @SuppressWarnings("rawtypes")
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);


        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


        // 设置value的序列化规则和 key的序列化规则
        //redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);


        // 7.设置 value 的转化格式和 key 的转化格式
        redisTemplate.setValueSerializer(new GenericToStringSerializer(Object.class));
        redisTemplate.setKeySerializer(new StringRedisSerializer());


        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);


        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
