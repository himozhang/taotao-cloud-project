package com.taotao.cloud.core.sensitive.word.exception;

/**
 */
public class SensitiveWordException extends RuntimeException {

    public SensitiveWordException() {
    }

    public SensitiveWordException(String message) {
        super(message);
    }

    public SensitiveWordException(String message, Throwable cause) {
        super(message, cause);
    }

    public SensitiveWordException(Throwable cause) {
        super(cause);
    }

    public SensitiveWordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
