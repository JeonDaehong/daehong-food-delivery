package com.example.makedelivery.common.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException() {
        super("이미 해당 아이디가 존재합니다.");
    }
}
