package com.zerobase.cms.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    WRONG_VERIFICATION(HttpStatus.BAD_REQUEST, "인증코드가 틀렸습니다."),
    EXPIRED_CODE(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료되었습니다."),
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "아이디나 패스워드를 확인해주세요. 혹은 아직 인증되지 않은 회원입니다."),
    NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    NOT_NULL(HttpStatus.BAD_REQUEST, "null이 아닌 값을 입력해주세요.");

    private final HttpStatus httpStatus;
    private final String detail;
}
