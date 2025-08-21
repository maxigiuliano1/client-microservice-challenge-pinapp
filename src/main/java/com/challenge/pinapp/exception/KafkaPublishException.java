package com.challenge.pinapp.exception;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(String msg) {
        super(msg);
    }
}
