package com.example.makedelivery.common.exception;

public class PasswordNotMatchedException  extends RuntimeException {
    public PasswordNotMatchedException () {
        super("입력하신 패스워드가 등록된 패스워드와 일치하지 않습니다.");
    }
}