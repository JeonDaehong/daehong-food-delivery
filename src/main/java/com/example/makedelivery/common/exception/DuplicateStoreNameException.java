package com.example.makedelivery.common.exception;

public class DuplicateStoreNameException extends RuntimeException {
    public DuplicateStoreNameException() {
        super("같은 매장명이 이미 존재합니다.");
    }

    public DuplicateStoreNameException(String message) {
        super(message + " : 매장명이 이미 존재합니다.");
    }
}
