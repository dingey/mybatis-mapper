package com.github.dingey.mybatis.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ClassUtil {
    private ClassUtil() {
    }

    static <T extends Annotation, E> Optional<Field> getByAnnotation(Class<T> annotationClass, Class<E> eClass) {
        return getDeclaredFields(eClass).stream().filter(field -> field.isAnnotationPresent(annotationClass)).findFirst();
    }

    static List<Field> getDeclaredFields(Class<?> t) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class && clazz != Field.class; clazz = clazz.getSuperclass()) {
            try {
                for (Field f : clazz.getDeclaredFields()) {
                    int modifiers = f.getModifiers();
                    if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isTransient(modifiers)) {
                        fields.add(f);
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return fields;
    }
}
