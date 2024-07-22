package com.booker.core.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements CodeInfo {

    ADMIN("관리자"),
    NORMAL_USER("일반 유저")
    ;

    private final String description;
}
