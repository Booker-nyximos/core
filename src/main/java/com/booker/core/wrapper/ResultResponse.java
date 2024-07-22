package com.booker.core.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ResultResponse<T> implements ResultInterface<T> {
    private int status;
    private int code;
    private String message;
    private T result;

    public ResultResponse() {
        this.status = HttpStatus.OK.value();
        this.message = "success";
    }

    public ResultResponse(T result) {
        this.status = HttpStatus.OK.value();
        this.message = "success";
        this.result = result;
    }

    public ResultResponse(HttpStatus httpStatus) {
        this.status = httpStatus.value();
    }

    public ResultResponse(int code, String message) {
        this.status = HttpStatus.OK.value();
        this.code = code;
        this.message = message;
    }
    @Override
    public T getResult() {
        return result;
    }
}
