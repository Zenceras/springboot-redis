package net.ilchenko.redis.app.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;

    ApiError(Throwable th) {
        this.message = th.getMessage();
    }

    ApiError(String message) {
        this.message = message;
    }
}
