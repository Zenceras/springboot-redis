package net.ilchenko.redis.app.exceptions;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    public AppException(String messageKey, Object... args) {
        this.messageKey = messageKey;
        this.args = args;
    }

    public AppException(Throwable cause, String messageKey, Object... args) {
        this(messageKey, args);
        initCause(cause);
    }
}
