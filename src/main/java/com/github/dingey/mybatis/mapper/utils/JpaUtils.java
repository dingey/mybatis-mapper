package com.github.dingey.mybatis.mapper.utils;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
public final class JpaUtils {
    private static final Map<Class<?>, String> columns = new WeakHashMap<>();
    private static final Map<SerializedLambda, String> lambdaColumnMap = new WeakHashMap<>();

    private JpaUtils() {
    }

    public static String table(Class<?> bean) {
        String name;
        if (bean.isAnnotationPresent(Table.class) && !bean.getAnnotation(Table.class).name().isEmpty()) {
            name = bean.getAnnotation(Table.class).name();
        } else {
            if (Const.camelCase) {
                name = StringUtils.snakeCase(bean.getSimpleName());
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

    public static boolean isSequenceId(Field f) {
        return f.isAnnotationPresent(SequenceGenerator.class) || (f.isAnnotationPresent(Id.class) && f.getDeclaringClass().isAnnotationPresent(SequenceGenerator.class));
    }

    public static String table(Object bean) {
        return table(bean.getClass());
    }

    public static String column(Field f) {
        if (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).name().isEmpty()) {
            return f.getAnnotation(Column.class).name();
        } else {
            return column(f.getName());
        }
    }

    public static String column(String name) {
        if (Const.camelCase) {
            name = StringUtils.snakeCase(name);
        }
        if (Const.columnUpper) {
            name = name.toUpperCase();
        }
        return name;
    }

    public static String column(SerializedLambda lambda) {
        String col = lambdaColumnMap.get(lambda);
        if (col == null) {
            synchronized (lambdaColumnMap) {
                col = lambdaColumnMap.get(lambda);
                if (col == null) {
                    Field lambdaField = ClassUtils.getLambdaField(lambda);
                    col = column(lambdaField);
                    lambdaColumnMap.put(lambda, col);
                }
            }
        }
        return col;
    }

    public static boolean insertable(Field field) {
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

    public static boolean updatable(Field field) {
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

    public static boolean selectable(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public static String getColumnString(Class<?> clazz) {
        if (clazz == null) {
            return "*";
        }
        String s = columns.get(clazz);
        if (s == null) {
            StringBuilder sql = new StringBuilder();
            for (Field f : ClassUtils.getDeclaredFields(clazz)) {
                if (!JpaUtils.selectable(f)) {
                    continue;
                }
                sql.append(JpaUtils.column(f)).append(", ");
            }
            sql.delete(sql.length() - 2, sql.length() - 1);
            s = sql.toString();
            columns.put(clazz, s);
        }
        return s;
    }
}
