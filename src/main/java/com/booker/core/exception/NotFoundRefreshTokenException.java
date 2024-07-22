package com.booker.core.exception;

import com.booker.core.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundRefreshTokenException extends BookerException {

    public NotFoundRefreshTokenException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_REFRESH_TOKEN);
    }

    public NotFoundRefreshTokenException(ErrorCode errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }

    public NotFoundRefreshTokenException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(httpStatus, errorCode);
    }
}
