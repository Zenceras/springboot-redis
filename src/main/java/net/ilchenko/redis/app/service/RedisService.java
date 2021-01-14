package net.ilchenko.redis.app.service;

import java.time.Instant;
import java.util.List;

public interface RedisService {
    Instant pushMessageToRedis(String message);

    String getLastMessageFromRedis();

    List<String> getMessagesByTimeFromRedis(Instant start, Instant end​​);
}
