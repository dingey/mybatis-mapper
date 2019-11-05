package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * mybatis通用mapper接口
 */
@SuppressWarnings("all")
public class SqlProvider {
    private static final HashMap<String, String> sqls = new HashMap<>();
    private static final HashMap<String, List<Field>> modelFieldsMap = new HashMap<>();
    private static final HashMap<Class<?>, Field> idFieldsMap = new HashMap<>();
    private static final HashMap<Field, Method> READ_METHOD = new HashMap<>();

    private SqlProvider() {
    }

    /**
     * 获取insertSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String insert(Object bean) {
        if (Jpa.elClass(bean)) {
            return getInsertSql(bean, false);
        } else {
            return cachedSql(bean, "insert", t -> getInsertSql(bean, false));
        }
    }

    /**
     * 获取insertSQL，忽略null列
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String insertSelective(Object bean) {
        return getInsertSql(bean, true);
    }

    /**
     * 获取insertSQL
     *
     * @param bean      模型对象
     * @param selective 忽略null
     * @return SQL
     */
    public static String getInsertSql(Object bean, boolean selective) {
        StringBuilder sql = new StringBuilder();
        List<String> props = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        sql.append("insert into ").append(Jpa.table(bean)).append("(");
        try {
            for (Field field : getCachedModelFields(bean.getClass())) {
                if (selective) {
                    Object value = readValue(field, bean);
                    if (value == null && !Jpa.isSequenceId(field)) {
                        continue;
                    }
                }
                if (!Jpa.insertable(field)) {
                    continue;
                }
                columns.add(Jpa.column(field));
                props.add("#{" + field.getName() + "}");
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i));
            if (i != columns.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")").append(" values(");
        for (int i = 0; i < props.size(); i++) {
            sql.append(props.get(i));
            if (i != props.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return sql.toString();
    }

    /**
     * 获取updateSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String update(Object bean) {
        if (Jpa.elClass(bean)) {
            return getUpdateSql(bean, false);
        } else {
            return cachedSql(bean, "update", t -> getUpdateSql(bean, false));
        }
    }

    /**
     * 获取updateSQL,忽略null
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String updateSelective(Object bean) {
        return getUpdateSql(bean, true);
    }

    /**
     * 获取updateSQL
     *
     * @param bean      模型对象
     * @param selective 忽略null
     * @return SQL
     */
    public static String getUpdateSql(Object bean, boolean selective) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(Jpa.table(bean)).append(" set ");
        Field id = null;
        Field version = null;
        try {
            for (Field field : getCachedModelFields(bean.getClass())) {
                if (selective) {
                    Object value = readValue(field, bean);
                    if (value == null) {
                        continue;
                    }
                }
                if (field.isAnnotationPresent(Id.class)) {
                    id = field;
                    continue;
                } else if (field.isAnnotationPresent(Version.class)) {
                    version = field;
                    String column = Jpa.column(field);
                    sql.append(column).append("=").append(column).append("+1,");
                    continue;
                } else if (!Jpa.updatable(field)) {
                    continue;
                }
                sql.append(Jpa.column(field)).append("=#{").append(field.getName()).append("},");
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(Const.WHERE);
        sql.append(Jpa.column(id)).append(" =#{").append(id.getName()).append(Const.AND1);
        if (version != null) {
            sql.append(Const.AND).append(Jpa.column(version)).append("=#{").append(version.getName()).append("} and");
        }
        return sql.delete(sql.length() - 4, sql.length()).toString();
    }

    /**
     * 获取deleteSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String delete(Object bean) {
        if (Jpa.elClass(bean)) {
            return getDeleteSql(bean);
        } else {
            return cachedSql(bean, "delete", t -> getDeleteSql(bean));
        }
    }

    private static String getDeleteSql(Object bean) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(Jpa.table(bean)).append(Const.WHERE);
        Field id = null;
        try {
            for (Field field : getCachedModelFields(bean.getClass())) {
                if (field.isAnnotationPresent(Id.class)) {
                    id = field;
                }
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        if (id == null) {
            throw new MapperException("id未声明" + bean.getClass().getName());
        } else {
            sql.append(Jpa.column(id)).append("=#{").append(id).append(Const.AND1);
        }
        return sql.delete(sql.length() - 5, sql.length()).toString();
    }

    /**
     * 获取deleteSQL
     *
     * @param context context
     * @return SQL
     */
    public static String deleteMark(ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("deleteMark不支持的方式" + entity.getName());
        }
        return getCachedSql(entity, "deleteMark", t -> getDeleteMarkSql(entity));
    }

    /**
     * 获取deleteSQL,支持表名变量
     *
     * @param bean bean
     * @return SQL
     */
    public static String deleteMarked(Object bean) {
        if (Jpa.elClass(bean)) {
            return getDeleteMarkSql(bean);
        } else {
            return getCachedSql(bean.getClass(), "deleteMarked", t -> getDeleteMarkSql(bean));
        }
    }

    private static String getDeleteMarkSql(Class<?> entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(Jpa.table(entity)).append(" set ");
        Field delete = null;
        Field version = null;
        Field id = null;
        DeleteMark mark = null;
        try {
            for (Field field : getCachedModelFields(entity)) {
                if (field.isAnnotationPresent(DeleteMark.class)) {
                    delete = field;
                    mark = field.getAnnotation(DeleteMark.class);
                } else if (field.isAnnotationPresent(Id.class)) {
                    id = field;
                } else if (field.isAnnotationPresent(Version.class)) {
                    version = field;
                }
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        sql.append(Jpa.column(delete)).append("=").append(mark.value()).append(" where ");
        if (id == null) {
            throw new MapperException("主键必须声明");
        } else {
            sql.append(Jpa.column(id)).append("=#{").append(id.getName()).append(Const.AND1);
            if (version != null) {
                sql.append(Jpa.column(version)).append("=#{").append(version.getName()).append(Const.AND1);
            }
        }
        return sql.delete(sql.length() - 5, sql.length()).toString();
    }

    private static String getDeleteMarkSql(Object entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(Jpa.table(entity)).append(" set ");
        Field delete = null;
        Field version = null;
        Field id = null;
        DeleteMark mark = null;
        try {
            for (Field field : getCachedModelFields(entity.getClass())) {
                if (field.isAnnotationPresent(DeleteMark.class)) {
                    delete = field;
                    mark = field.getAnnotation(DeleteMark.class);
                } else if (field.isAnnotationPresent(Id.class)) {
                    id = field;
                } else if (field.isAnnotationPresent(Version.class)) {
                    version = field;
                }
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        sql.append(Jpa.column(delete)).append("=").append(mark.value()).append(" where ");
        if (id == null) {
            throw new MapperException("主键必须声明");
        } else {
            sql.append(Jpa.column(id)).append("=#{").append(id.getName()).append(Const.AND1);
            if (version != null) {
                sql.append(Jpa.column(version)).append("=#{").append(version.getName()).append(Const.AND1);
            }
        }
        return sql.delete(sql.length() - 5, sql.length()).toString();
    }

    /**
     * 获取selectSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String get(Object bean) {
        if (Jpa.elClass(bean)) {
            return getGetSql(bean);
        } else {
            return cachedSql(bean, "get", t -> getGetSql(bean));
        }
    }

    private static String getGetSql(Object bean) {
        StringBuilder sql = new StringBuilder();
        sql.append(Const.SELECT_FROM).append(Jpa.table(bean)).append(Const.WHERE);
        try {
            Field id = id(bean.getClass());
            sql.append(Jpa.column(id)).append("=#{").append(id.getName()).append("}");
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        return sql.toString();
    }

    /**
     * 获取selectSQL
     *
     * @param context context
     * @return SQL
     */
    public static String getById(ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("getById方法不支持表名变量" + entity.getName());
        }
        return getCachedSql(entity, "getById", t -> {
            StringBuilder sql = new StringBuilder();
            sql.append(Const.SELECT_FROM).append(Jpa.table(entity)).append(Const.WHERE);
            try {
                for (Field field : getCachedModelFields(entity)) {
                    if (field.isAnnotationPresent(Id.class)) {
                        sql.append(Jpa.column(field)).append("=#{param1}");
                        break;
                    }
                }
            } catch (Exception e) {
                throw new MapperException(sql.toString(), e);
            }
            return sql.toString();
        });
    }

    /**
     * 获取selectSQL
     *
     * @param context context
     * @return SQL
     */
    public static String deleteById(ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("deleteById方法不支持表名变量" + entity.getName());
        }
        return getCachedSql(entity, "deleteById", t -> {
            StringBuilder sql = new StringBuilder();
            sql.append("delete from ").append(Jpa.table(entity)).append(Const.WHERE);
            try {
                for (Field field : getCachedModelFields(entity)) {
                    if (field.isAnnotationPresent(Id.class)) {
                        sql.append(Jpa.column(field)).append("=#{param1}");
                        break;
                    }
                }
            } catch (Exception e) {
                throw new MapperException(sql.toString(), e);
            }
            return sql.toString();
        });
    }
    /**
     * 获取selectSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String list(Object bean) {
        StringBuilder sql = new StringBuilder();
        sql.append(Const.SELECT_FROM).append(Jpa.table(bean)).append(" where 1=1 ");
        String orderby = null;
        try {
            for (Field f : getCachedModelFields(bean.getClass())) {
                if (Jpa.selectable(f)) {
                    Object v = readValue(f, bean);
                    if (v == null) {
                    } else if (f.isAnnotationPresent(OrderBy.class)) {
                        orderby = String.valueOf(v);
                    } else {
                        sql.append(Const.AND).append(Jpa.column(f)).append("=#{").append(f.getName()).append("}");
                    }
                }
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        if (orderby != null) {
            if (orderby.contains("order by")) {
                sql.append(orderby);
            } else {
                sql.append(" order by ").append(orderby);
            }
        }
        return sql.toString();
    }

    /**
     * 获取selectcountSQL
     *
     * @param bean 模型对象
     * @return SQL
     */
    public static String count(Object bean) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(0) from ").append(Jpa.table(bean)).append(" where 1=1 ");
        try {
            for (Field f : getCachedModelFields(bean.getClass())) {
                if (Jpa.selectable(f) && readValue(f, bean) != null) {
                    sql.append(Const.AND).append(Jpa.column(f)).append("=#{").append(f.getName()).append("}");
                }
            }
        } catch (Exception e) {
            throw new MapperException(sql.toString(), e);
        }
        return sql.toString();
    }

    /**
     * 获取selectSQL
     *
     * @param context context
     * @return SQL
     */
    public static String listAll(ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("listAll方法不支持表名变量" + entity.getName());
        }
        return getCachedSql(entity, "listAll", t -> Const.SELECT_FROM + Jpa.table(entity));
    }

    /**
     * 获取selectSQL
     *
     * @param context context
     * @return SQL
     */
    public static String countAll(ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("countAll方法不支持表名变量" + entity.getName());
        }
        return getCachedSql(entity, "countAll", t -> "select count(0) from " + Jpa.table(entity));
    }

    /**
     * 获取selectSQL
     *
     * @param ids     主键
     * @param context context
     * @return SQL
     */
    public static String listByIds(@Param("ids") Iterable<Serializable> ids, ProviderContext context) {
        Class<?> entity = MapperMethod.entity(context);
        if (Jpa.elClass(entity)) {
            throw new MapperException("listByIds方法不支持表名变量" + entity.getName());
        }
        StringBuilder s = new StringBuilder();
        s.append(Const.SELECT_FROM).append(Jpa.table(entity)).append(Const.WHERE);
        s.append(Jpa.column(id(entity))).append(" in ( ");
        for (Serializable id : ids) {
            s.append("'").append(id).append("',");
        }
        s.deleteCharAt(s.length() - 1).append(" )");
        return s.toString();
    }

    /**
     * 获取主键field
     *
     * @param entity 模型对象
     * @return SQL
     */
    public static Field id(Class<?> entity) {
        Field id = null;
        if (idFieldsMap.containsKey(entity)) {
            id = idFieldsMap.get(entity);
        } else {
            for (Field f : getCachedModelFields(entity)) {
                if (f.isAnnotationPresent(Id.class)) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    id = f;
                    idFieldsMap.put(entity, f);
                    break;
                }
            }
        }
        if (id == null) {
            throw new MapperException(entity.getName() + "没有主键!");
        }
        return id;
    }

    /**
     * 获取缓存的sql
     *
     * @param bean   对象实例
     * @param method 方法
     * @param func   func
     * @return SQL
     */
    private static String cachedSql(Object bean, String method, Func<Object> func) {
        String key = bean.getClass().getName() + "_" + method;
        String sql = sqls.get(key);
        if (sql == null) {
            sql = func.apply(bean);
            sqls.put(key, sql);
        }
        return sql;
    }

    /**
     * 获取缓存的sql
     *
     * @param bean   对象类
     * @param method 方法
     * @param func   func
     * @return SQL
     */
    private static String getCachedSql(Class<?> bean, String method, Func<Class<?>> func) {
        String k = bean.getName() + "_" + method;
        if (sqls.containsKey(k)) {
            return sqls.get(k);
        } else {
            String apply = func.apply(bean);
            sqls.put(k, apply);
            return apply;
        }
    }

    /**
     * 获取缓存fields
     *
     * @param beanClass 对象类
     * @return SQL
     */
    private static List<Field> getCachedModelFields(Class<?> beanClass) {
        if (modelFieldsMap.containsKey(beanClass.getName())) {
            return modelFieldsMap.get(beanClass.getName());
        } else {
            List<Field> fields = ClassUtil.getDeclaredFields(beanClass);
            fields.forEach(f -> f.setAccessible(true));
            modelFieldsMap.put(beanClass.getName(), fields);
            return fields;
        }
    }

    private static Object readValue(Field f, Object o) {
        Method method = READ_METHOD.get(f);
        if (method == null) {
            method = ClassUtil.getReadMethod(f, o);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            READ_METHOD.put(f, method);
        }
        try {
            return method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MapperException("获取值失败" + e.getMessage(), e);
        }
    }
}
