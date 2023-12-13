package com.example.makedelivery.common.exception;

public class MenuGroupDeletionException extends RuntimeException {
    public MenuGroupDeletionException() {
        super("메뉴가 하나라도 남아있으면, 메뉴 그룹을 삭제할 수 없습니다.");
    }
}
