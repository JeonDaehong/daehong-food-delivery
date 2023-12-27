package com.example.makedelivery.common.exception;

import lombok.Builder;

@Builder
public class ExceptionDto {
    private String errorCode;
    private String errorMessage;
}
