package com.booker.core.exception;

import com.booker.core.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BookerException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.NOT_FOUND_REFRESH_TOKEN);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(HttpStatus.UNAUTHORIZED, errorCode);
    }

    public UnauthorizedException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(httpStatus, errorCode);
    }

}
