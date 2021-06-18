package com.github.dingey.mybatis.mapper.utils;

import com.github.dingey.mybatis.mapper.exception.MapperException;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ClassUtils {
    static final Map<Class<?>, List<Field>> fieldsMap = new HashMap<>();
    static final Map<SerializedLambda, Field> lambdaFieldMap = new HashMap<>();

    private ClassUtils() {
    }

    public static <T extends Annotation, E> Optional<Field> getByAnnotation(Class<T> annotationClass, Class<E> eClass) {
        return getDeclaredFields(eClass).stream().filter(field -> field.isAnnotationPresent(annotationClass)).findFirst();
    }

    public static List<Field> getDeclaredFields(Class<?> t) {
        List<Field> fields = fieldsMap.get(t);
        if (fields == null) {
            synchronized (fieldsMap) {
                fields = fieldsMap.get(t);
                if (fields == null) {
                    fields = new ArrayList<>();
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
                    fieldsMap.put(t, fields);
                }
            }
        }
        return fields;
    }

    public static Field getLambdaField(SerializedLambda lambda) {
        Field field = lambdaFieldMap.get(lambda);
        if (field == null) {
            try {
                Class<?> aClass = Class.forName(lambda.getImplClass().replaceAll("/", "."));
                String name = lambda.getImplMethodName();
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get", "");
                } else {
                    name = name.replaceFirst("is", "");
                }
                name = StringUtils.firstLower(name);
                for (Field f : getDeclaredFields(aClass)) {
                    if (f.getName().equals(name)) {
                        field = f;
                    }
                }
                if (field == null) {
                    throw new MapperException("不存在字段" + name);
                } else {
                    lambdaFieldMap.put(lambda, field);
                }
            } catch (ClassNotFoundException e) {
                throw new MapperException(e.getMessage(), e);
            }
        }
        return field;
    }
}
