package com.github.dingey.mybatis.mapper;

import javax.persistence.*;
import java.lang.reflect.Field;

@SuppressWarnings("unused")
class Jpa {

    private Jpa() {
    }

    static String table(Class<?> bean) {
        String name;
        if (bean.isAnnotationPresent(Table.class) && !bean.getAnnotation(Table.class).name().isEmpty()) {
            name = bean.getAnnotation(Table.class).name();
        } else {
            if (Const.camelCase) {
                name = StringUtil.snakeCase(bean.getSimpleName());
            } else {
                name = bean.getSimpleName();
            }
            if (Const.tablePrefix != null) {
                name = Const.tablePrefix + name;
            }
            if (Const.tableUpper) {
                name = name.toUpperCase();
            }
        }
        return name;
    }

    static boolean isSequenceId(Field f) {
        return f.isAnnotationPresent(SequenceGenerator.class) || (f.isAnnotationPresent(Id.class) && f.getDeclaringClass().isAnnotationPresent(SequenceGenerator.class));
    }

    static String table(Object bean) {
        return table(bean.getClass());
    }

    static String column(Field f) {
        String name;
        if (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).name().isEmpty()) {
            name = f.getAnnotation(Column.class).name();
        } else {
            if (Const.camelCase) {
                name = StringUtil.snakeCase(f.getName());
            } else {
                name = f.getName();
            }
            if (Const.columnUpper) {
                name = name.toUpperCase();
            }
        }
        return name;
    }

    static boolean insertable(Field field) {
        boolean insert;
        if (field.isAnnotationPresent(Transient.class)) {
            insert = false;
        } else if (field.isAnnotationPresent(Column.class)) {
            insert = field.getDeclaredAnnotation(Column.class).insertable();
        } else {
            insert = true;
        }
        return insert;
    }

    static boolean updatable(Field field) {
        boolean update;
        if (field.isAnnotationPresent(Transient.class)) {
            update = false;
        } else if (field.isAnnotationPresent(Column.class)) {
            update = field.getDeclaredAnnotation(Column.class).updatable();
        } else {
            update = true;
        }
        return update;
    }

    static boolean selectable(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}
