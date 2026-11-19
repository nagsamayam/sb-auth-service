package com.example.demo.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean aBoolean) {
        if (aBoolean == null) {
            return null;
        }
        return aBoolean ? "Yes" : "No";
    }

    @Override
    public Boolean convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return "Yes".equals(s);
    }
}
