package com.example.makedelivery.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpStatusResponseConstants {

    public static final ResponseEntity<HttpStatus> RESPONSE_OK = ResponseEntity.status(HttpStatus.OK).build(); // 200
    public static final ResponseEntity<HttpStatus> RESPONSE_CONFLICT = ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    public static final ResponseEntity<HttpStatus> RESPONSE_BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400
    public static final ResponseEntity<HttpStatus> RESPONSE_NOT_FOUND = ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404

}
