package com.booker.core.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType implements CodeInfo {
    ACCESS_TOKEN("access"),
    REFRESH_TOKEN("refresh");

    private final String description;
}
