package com.example.makedelivery.common.exception;

import com.amazonaws.SdkClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

/**
 * RestControllerAdvice 는 프로젝트의 전역에서 발생할 수 있는 에러에 대한 처리를 도와줍니다.
 * ExceptionHandler 를 통해 Exception 을 지정할 수 있으며,
 * 해당 Exception 이 동작할 경우 공통된 처리 동작을 실행해줍니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Custom Exception 인 ApiException 이 호출 되었을 경우 동작합니다.
     * Enum 에 미리 저장된, HttpStatus 와 Code 그리고 메시지가
     * 클라이언트에게 return 됩니다.
     */
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(final ApiException e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(e.getError().getCode())
                        .errorMessage(e.getError().getMessage())
                        .build());
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(final HttpClientErrorException e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(LOGIN_SECURITY_ERROR.getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(LOGIN_SECURITY_ERROR.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(final IOException e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(IMG_UPLOAD_ERROR.getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(IMG_UPLOAD_ERROR.getCode())
                        .errorMessage(IMG_UPLOAD_ERROR.getMessage())
                        .build());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(final RuntimeException e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(RUNTIME_EXCEPTION.getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(RUNTIME_EXCEPTION.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(final Exception e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(INTERNAL_SERVER_ERROR.getCode())
                        .errorMessage(e.getMessage())
                        .build());
    }


}
