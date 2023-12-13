package com.example.makedelivery.common.exception;

public class MainAddressDeletionException extends RuntimeException {
    public MainAddressDeletionException() {
        super("메인 주소는 삭제할 수 없습니다.");
    }
}
