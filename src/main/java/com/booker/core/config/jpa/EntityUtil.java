package com.booker.core.config.jpa;

public class EntityUtil {
    public static Boolean generateDDL(JpaProperty jpaProperty) {
        return "update".equals(jpaProperty.getProperties().getProperty("hibernate.ddl-auto"));
    }

    public static Boolean showSQL(JpaProperty jpaProperty) {
        return "true".equals(jpaProperty.getProperties().getProperty("hibernate.show-sql"));
    }
}
