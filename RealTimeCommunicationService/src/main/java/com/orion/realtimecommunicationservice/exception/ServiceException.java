package com.orion.realtimecommunicationservice.exception;


/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
public class ServiceException extends BaseException{
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
