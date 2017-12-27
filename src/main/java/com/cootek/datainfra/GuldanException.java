package com.cootek.datainfra;

public class GuldanException extends RuntimeException {
    public GuldanException(String msg) {
        super(msg);
    }

    public GuldanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
