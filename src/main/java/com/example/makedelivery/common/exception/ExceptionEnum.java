package com.example.makedelivery.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionEnum {

    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E_0001", "잘못 된 요청입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0002", "해당 회원을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E_0003", "서버에 문제가 발생하였습니다."),
    ADDR_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0004", "해당 주소를 찾을 수 없습니다."),
    MENU_GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0005", "해당 메뉴그룹을 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0006", "해당 메뉴를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0007", "해당 매장 정보를 찾을 수 없습니다."),
    OPTN_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0008", "해당 옵션 정보를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "B_0001", "중복된 아이디(이메일) 입니다."),
    DUPLICATED_STORE_NAME(HttpStatus.CONFLICT, "B_0002", "중복된 매장 이름입니다."),
    MAIN_ADDR_DELETE(HttpStatus.CONFLICT, "B_0003", "메인 주소는 삭제할 수 없습니다."),
    MENU_GROUP_DELETE(HttpStatus.CONFLICT, "B_0004", "메뉴가 하나라도 남아있으면, 메뉴그룹을 삭제할 수 없습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.CONFLICT, "B_0005", "패스워드가 일치하지 않습니다."),
    MEMBER_STATUS_DELETE(HttpStatus.CONFLICT, "B_0006", "해당 아이디는 삭제 처리된 상태입니다. 관리자에게 문의해주시기 바랍니다."),
    MEMBER_STATUS_STOPPED(HttpStatus.CONFLICT, "B_0007", "해당 아이디는 정지 처리된 상태입니다. 관리자에게 문의해주시기 바랍니다."),
    LOGIN_SECURITY_ERROR(HttpStatus.UNAUTHORIZED, "S_0001", "로그인 정보가 올바르지 않습니다. ID, PW를 다시 입력해주시기 바랍니다."),
    STORE_SECURITY_ERROR(HttpStatus.UNAUTHORIZED, "S_0002", "해당 매장에 대한 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
