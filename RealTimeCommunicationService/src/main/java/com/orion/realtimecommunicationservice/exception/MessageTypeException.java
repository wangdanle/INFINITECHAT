package com.orion.realtimecommunicationservice.exception;


/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
public class MessageTypeException extends BaseException{
    public MessageTypeException() {
    }

    public MessageTypeException(String message) {
        super(message);
    }

    public MessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTypeException(Throwable cause) {
        super(cause);
    }

    public MessageTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
