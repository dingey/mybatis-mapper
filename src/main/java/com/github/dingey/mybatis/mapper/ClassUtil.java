package com.github.dingey.mybatis.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class ClassUtil {
    private ClassUtil() {
    }

    static Method getReadMethod(Field f, Object o) {
        String n = StringUtil.firstUpper(f.getName());
        for (Class<?> clazz = o.getClass(); clazz != Object.class && clazz != Class.class && clazz != Field.class; clazz = clazz.getSuperclass()) {
            if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                try {
                    return clazz.getDeclaredMethod("is" + n);
                } catch (NoSuchMethodException e) {
                    try {
                        return clazz.getDeclaredMethod("get" + n);
                    } catch (NoSuchMethodException ignore) {
                    }
                }
            } else {
                try {
                    return clazz.getDeclaredMethod("get" + n);
                } catch (NoSuchMethodException ignore) {
                }
            }
        }
        throw new MapperException("类" + o.getClass().getName() + "属性" + f.getName() + "不存在读的方法");
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
