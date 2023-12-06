package com.example.makedelivery.common.exception;
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("해당 회원은 존재하지 않습니다.");
    }
}