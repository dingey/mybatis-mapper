package com.github.dingey.mybatis.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class ClassUtil {
    private ClassUtil() {
    }

    static Field getDeclaredField(Class<?> t, String name) {
        for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class && clazz != Field.class; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (field != null) {
                    return field;
                }
            } catch (NoSuchFieldException ignore) {
            }
        }
        throw new MapperException("类" + t.getName() + "找不到属性" + name);
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
