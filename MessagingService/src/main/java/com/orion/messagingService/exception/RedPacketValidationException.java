package com.orion.messagingService.exception;

public class RedPacketValidationException extends RuntimeException {
    public RedPacketValidationException() {
    }

    public RedPacketValidationException(String message) {
        super(message);
    }

    public RedPacketValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedPacketValidationException(Throwable cause) {
        super(cause);
    }

    public RedPacketValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
