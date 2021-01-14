package net.ilchenko.redis.app.controller;

import lombok.RequiredArgsConstructor;
import net.ilchenko.redis.app.dto.MessageDto;
import net.ilchenko.redis.app.service.RedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessagesController {

    private final RedisService redisService;

    @PostMapping("/publish")
    public Instant publishMessage(@Valid @RequestBody MessageDto messageDto) {
        return redisService.pushMessageToRedis(messageDto.getContent());
    }

    @GetMapping("/getLast")
    public String getLastMessage() {
        return redisService.getLastMessageFromRedis();
    }

    @GetMapping("/getByTime")
    public List<String> getMessagesByTime(@RequestParam Instant start​,
                                          @RequestParam Instant end​) {
        return redisService.getMessagesByTimeFromRedis(start​, end​);
    }
}
