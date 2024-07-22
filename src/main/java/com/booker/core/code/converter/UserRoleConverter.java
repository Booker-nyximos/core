package com.booker.core.code.converter;

import com.booker.core.code.UserRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter extends EnumConverter<UserRole> {

    public UserRoleConverter() {
        super(UserRole.class);
    }

    public UserRoleConverter(Class<UserRole> enumClass) {
        super(enumClass);
    }

}
