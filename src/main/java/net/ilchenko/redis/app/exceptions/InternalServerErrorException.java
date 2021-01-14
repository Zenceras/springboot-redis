package net.ilchenko.redis.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends AppException {
    public InternalServerErrorException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
