package com.booker.core.exception;

import com.booker.core.exception.code.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BookerException extends RuntimeException {

    private HttpStatus httpStatus;
    private String message;

    public BookerException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.httpStatus = httpStatus;
    }

}