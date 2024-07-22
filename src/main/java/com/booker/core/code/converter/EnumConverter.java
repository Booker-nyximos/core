package com.booker.core.code.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.NoSuchElementException;

@Converter
public class EnumConverter<E extends Enum<E>> implements AttributeConverter<Enum, String> {

    private Class<E> clz;

    public EnumConverter(){}
    public EnumConverter(Class<E> enumClass){
        this.clz = enumClass;
    }

    @Override
    public Enum<E> convertToEntityAttribute(String name) {
        return StringUtils.isEmpty(name) || StringUtils.isAllBlank(name)?null:EnumSet.allOf(clz).stream()
                .filter(e->e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(name));
    }

    @Override
    public String convertToDatabaseColumn(Enum enumClass) {
        return ObjectUtils.isEmpty(enumClass) ? null : EnumSet.allOf(clz).stream()
                .filter(e->e.ordinal() == enumClass.ordinal())
                .findAny()
                .map(Enum::name)
                .orElseThrow(() -> new NoSuchElementException(enumClass.name()));
    }
}
