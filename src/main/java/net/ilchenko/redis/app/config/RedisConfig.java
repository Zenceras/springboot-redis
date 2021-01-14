package net.ilchenko.redis.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String REDIS_HOSTNAME;

    @Value("${spring.redis.port}")
    private int REDIS_PORT;

    @Value("${spring.redis.password}")
    private String REDIS_PASSWORD;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        log.debug("REDIS HOST: {}", REDIS_HOSTNAME);
        log.debug("REDIS PORT: {}", REDIS_PORT);
        log.debug("REDIS PASSWORD: {}", REDIS_PASSWORD);

        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
        final JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .usePooling().build();
        if (REDIS_PASSWORD != null) configuration.setPassword(REDIS_PASSWORD);
        final JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }
}
