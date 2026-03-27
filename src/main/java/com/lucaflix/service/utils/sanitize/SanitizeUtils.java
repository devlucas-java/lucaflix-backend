package com.lucaflix.service.utils.sanitize;

import java.lang.reflect.Field;

public class SanitizeUtils {

    public static void sanitizeStrings(Object dto) {
        if (dto == null) return;

        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(dto);
                    if (value != null) {
                        field.set(dto, Sanitizer.sanitize(value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}