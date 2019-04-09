package com.github.dingey.mybatis.mapper;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;

@SuppressWarnings("unused")
class JPA {
    private JPA() {
    }

    static String table(Class<?> bean) {
        if (bean.isAnnotationPresent(Table.class) && !bean.getAnnotation(Table.class).name().isEmpty()) {
            return bean.getAnnotation(Table.class).name();
        } else {
            return StringUtil.snakeCase(bean.getSimpleName());
        }
    }

    static String table(Object bean) {
        return table(bean.getClass());
    }

    static boolean insertable(Field field) {
        return !(field.isAnnotationPresent(Transient.class) || (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).insertable())) || !field.isAnnotationPresent(Column.class);
    }

    static boolean updatable(Field field) {
        return !(field.isAnnotationPresent(Transient.class) || (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).updatable())) || !field.isAnnotationPresent(Column.class);
    }
}
