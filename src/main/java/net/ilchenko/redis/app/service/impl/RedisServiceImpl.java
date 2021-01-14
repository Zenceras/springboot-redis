package net.ilchenko.redis.app.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilchenko.redis.app.exceptions.InternalServerErrorException;
import net.ilchenko.redis.app.exceptions.MessageKeys;
import net.ilchenko.redis.app.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${application.redis.set-messages}")
    private String setMessages;

    /**
     * This method push new message to the Redis
     *
     * @param message - Message to push
     * @return        - Timestamp of this message
     */
    @Override
    public Instant pushMessageToRedis(String message) {
        try {
            final Instant timestamp = Instant.now();
            final double doubleTimestamp = getDouble(timestamp);
            redisTemplate.boundZSetOps(setMessages)
                    .add(addTimestampToMessage(message, doubleTimestamp), doubleTimestamp);
            return timestamp;
        } catch (Exception e) {
            log.error("Can't push message to Redis", e);
            throw new InternalServerErrorException(MessageKeys.CANT_PUSH_MESSAGE);
        }
    }

    /**
     * This method get last message from the Redis
     *
     * @return - last message from the Redis
     */
    @Override
    public String getLastMessageFromRedis() {
        try {
            final Set<String> result = redisTemplate.opsForZSet()
                    .reverseRangeByScore(setMessages, Double.MIN_VALUE, Double.MAX_VALUE, 0L, 1L);
            if (result != null && result.size() == 1) {
                return removeTimestampFromMessage(result.iterator().next());
            }
            return "";
        } catch (Exception e) {
            log.error("Can't get last message from Redis", e);
            throw new InternalServerErrorException(MessageKeys.CANT_GET_LAST_MESSAGE);
        }
    }

    /**
     * This method get all messages that from the Redis that occurred between two given timestamps
     *
     * @param start - Start timestamp to get messages from the Redis
     * @param end​​​​   - End timestamp to get messages from the Redis
     * @return      - List of messages that occurred between two timestamps
     */
    @Override
    public List<String> getMessagesByTimeFromRedis(@NonNull Instant start, @NonNull Instant end) {
        try {
            return redisTemplate.boundZSetOps(setMessages).rangeByScore(getDouble(start), getDouble(end)).stream()
                    .filter(Objects::nonNull)
                    .map(this::removeTimestampFromMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Can't get messages by time from Redis", e);
            throw new InternalServerErrorException(MessageKeys.CANT_GET_MESSAGES_BY_TIME);
        }
    }

    double getDouble(Instant timestamp) {
        return (double) timestamp.toEpochMilli();
    }

    /**
     * This Method adds timestamp to message before putting it to Redis - otherwise we can't put two similar
     * message to the Redis ZSet(so make each message unique by adding timestamp)
     * @param message   - message to add timestamp
     * @param timestamp - timestamp, which we add to message
     * @return          - concatenated timestamp:message
     */
    String addTimestampToMessage(@NonNull String message, double timestamp) {
        return timestamp + ":" + message;
    }

    /**
     * This Method removes timestamp from the Redis message
     * @param message - message from Redis to remove timestamp
     * @return        - original message without timestamp
     */
    String removeTimestampFromMessage(@NonNull String message) {
        return message.substring(message.indexOf(':') + 1);
    }
}
