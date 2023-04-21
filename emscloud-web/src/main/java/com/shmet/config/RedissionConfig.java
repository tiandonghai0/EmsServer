package com.shmet.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redission配置类
 */
@Configuration
public class RedissionConfig {

    private final String REDISSON_PREFIX = "redis://";
    private final RedisProperties redisProperties;

    public RedissionConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String url = REDISSON_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort();
        // 这里以单台redis服务器为例
        config.useSingleServer()
                .setAddress(url)
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());

        // 实际开发过程中应该为cluster或者哨兵模式，这里以cluster为例
        //String[] urls = {"127.0.0.1:6379", "127.0.0.2:6379"};
        //config.useClusterServers()
        //        .addNodeAddress(urls);

        try {
            return Redisson.create(config);
        } catch (Exception e) {
            System.out.println(e);
            //log.error("RedissonClient init redis url:[{}], Exception:", url, e);
            return null;
        }
    }

}
