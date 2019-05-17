package com.github.dingey.mybatis.mapper;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.HashMap;

class Jpa {
    private static final HashMap<Class<?>, String> CLASS_NAME = new HashMap<>();
    private static final HashMap<Field, String> FIELD_NAME = new HashMap<>();
    private static final HashMap<Field, Boolean> SEQUENCE = new HashMap<>();
    private static final HashMap<Field, Boolean> INSERTABLE = new HashMap<>();
    private static final HashMap<Field, Boolean> UPDATEABLE = new HashMap<>();

    private Jpa() {
    }

    static String table(Class<?> bean) {
        if (CLASS_NAME.containsKey(bean)) {
            return CLASS_NAME.get(bean);
        } else {
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
            CLASS_NAME.put(bean, name);
            return name;
        }
    }

    static boolean isSequenceId(Field f) {
        if (SEQUENCE.containsKey(f)) {
            return SEQUENCE.get(f);
        } else {
            boolean b = f.isAnnotationPresent(SequenceGenerator.class) || f.getDeclaringClass().isAnnotationPresent(SequenceGenerator.class);
            SEQUENCE.put(f, b);
            return b;
        }
    }

    public static String parseEL(String sql, Object bean) {
        if (sql.contains(Const.EL)) {
            int beginIndex = sql.indexOf(Const.EL);
            int endIndex = sql.indexOf("}", beginIndex);
            String n = sql.substring(beginIndex + 2, endIndex);
            Field field = ClassUtil.getDeclaredField(bean.getClass(), n);
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object o = field.get(bean);
                sql = sql.substring(0, beginIndex) + o + sql.substring(endIndex + 1);
                if (sql.contains(Const.EL)) {
                    sql = parseEL(sql, bean);
                }
                return sql;
            } catch (IllegalAccessException e) {
                throw new MapperException("获取" + n + "值失败" + e.getMessage());
            }
        } else {
            return sql;
        }
    }

    static String table(Object bean) {
        String table = table(bean.getClass());
        return parseEL(table, bean);
    }

    static String column(Field f) {
        String name = FIELD_NAME.get(f);
        if (name == null) {
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
            FIELD_NAME.put(f, name);
        }
        return name;
    }

    static boolean insertable(Field field) {
        Boolean insert = INSERTABLE.get(field);
        if (insert == null) {
            if (field.isAnnotationPresent(Transient.class)) {
                insert = false;
            } else if (field.isAnnotationPresent(Column.class)) {
                insert = field.getDeclaredAnnotation(Column.class).insertable();
            } else {
                insert = true;
            }
            INSERTABLE.put(field, insert);
        }
        return insert;
    }

    static boolean updatable(Field field) {
        Boolean update = UPDATEABLE.get(field);
        if (update == null) {
            if (field.isAnnotationPresent(Transient.class)) {
                update = false;
            } else if (field.isAnnotationPresent(Column.class)) {
                update = field.getDeclaredAnnotation(Column.class).updatable();
            } else {
                update = true;
            }
            UPDATEABLE.put(field, update);
        }
        return update;
    }
}
