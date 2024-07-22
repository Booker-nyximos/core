package com.booker.core.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REFRESH_TOKEN("유효한 refresh token 이 아닙니다."),
    NOT_FOUND_REFRESH_TOKEN("refresh 토큰을 찾을 수 없습니다."),
            ;

    private String defaultMessage;

}
