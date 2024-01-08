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
    IMG_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E_0009", "이미지 입출력도중 에러가 발생하였습니다. 관리자에게 문의해주시기 바랍니다."),
    POINTS_INSUFFICIENT(HttpStatus.BAD_REQUEST, "E_0010", "전환 할 포인트가 부족합니다."),
    INVALID_POINT_UNIT(HttpStatus.BAD_REQUEST, "E_0011", "포인트 전환 단위는 5,000원 단위이며, 최소 5,000원 이상이어야 전환 가능합니다."),
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0012", "지원하지 않는 결제 서비스입니다."),
    STORE_DELETED(HttpStatus.BAD_REQUEST, "E_0013", "주문하려는 상품을 판매하는 매장은 현재 영업이 중단 된 매장입니다."),
    STORE_CLOSED(HttpStatus.BAD_REQUEST, "E_0014", "해당 매장은 현재 폐점 상태이므로 주문 할 수 없습니다."),
    MENU_DELETED(HttpStatus.BAD_REQUEST, "E_0015", "해당 메뉴는 삭제된 메뉴이므로, 주문 할 수 없습니다."),
    MENU_HIDDEN(HttpStatus.BAD_REQUEST, "E_0016", "해당 메뉴는 매장 점주에 의하여 잠시 주문 할 수 없는 메뉴입니다."),
    MENU_PRICE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "E_0017", "실제 메뉴의 가격과, 장바구니에 저장된 메뉴의 가격이 일치하지 않습니다. 삭제 후 다시 장바구니에 넣어주시기 바랍니다."),
    OPTN_DELETED(HttpStatus.BAD_REQUEST, "E_0018", "해당 옵션은 삭제된 옵션이므로, 주문 할 수 없습니다."),
    OPTN_HIDDEN(HttpStatus.BAD_REQUEST, "E_0019", "해당 옵션은 매장 점주에 의하여 잠시 주문 할 수 없는 옵션입니다."),
    OPTN_PRICE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "E_0020", "실제 옵션의 가격과, 장바구니에 저장된 옵션의 가격이 일치하지 않습니다. 삭제 후 다시 장바구니에 넣어주시기 바랍니다."),
    OVER_POINT(HttpStatus.BAD_REQUEST, "E_0021", "구매 가격보다, 사용 포인트가 많을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "E_0022", "해당 주문 정보를 찾을 수 없습니다."),
    ORDER_CANCEL_ERROR(HttpStatus.BAD_REQUEST, "E_0023", "주문 완료, 주문 승인 상태의 경우에만 주문을 취소 할 수 있습니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "B_0001", "중복된 아이디(이메일) 입니다."),
    DUPLICATED_STORE_NAME(HttpStatus.CONFLICT, "B_0002", "중복된 매장 이름입니다."),
    MAIN_ADDR_DELETE(HttpStatus.CONFLICT, "B_0003", "메인 주소는 삭제할 수 없습니다."),
    MENU_GROUP_DELETE(HttpStatus.CONFLICT, "B_0004", "메뉴가 하나라도 남아있으면, 메뉴그룹을 삭제할 수 없습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.CONFLICT, "B_0005", "패스워드가 일치하지 않습니다."),
    MEMBER_STATUS_DELETE(HttpStatus.CONFLICT, "B_0006", "해당 아이디는 삭제 처리된 상태입니다. 관리자에게 문의해주시기 바랍니다."),
    MEMBER_STATUS_STOPPED(HttpStatus.CONFLICT, "B_0007", "해당 아이디는 정지 처리된 상태입니다. 관리자에게 문의해주시기 바랍니다."),
    CART_STORE_NOT_MATCHED(HttpStatus.CONFLICT, "B_0008", "장바구니에는 같은 매장의 메뉴만 한 번에 담을 수 있습니다. 다른 매장의 메뉴를 담으시려면 장바구니를 비워주시기 바랍니다."),
    LOGIN_SECURITY_ERROR(HttpStatus.UNAUTHORIZED, "S_0001", "로그인 정보가 올바르지 않습니다. ID, PW를 다시 입력해주시기 바랍니다."),
    STORE_SECURITY_ERROR(HttpStatus.UNAUTHORIZED, "S_0002", "해당 매장에 대한 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
