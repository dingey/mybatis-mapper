package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author d
 * @since 0.4.0
 */
public class SourceProvider<T> {
    private final Class<T> clazz;
    final Log log = LogFactory.getLog(SequenceInterceptor.class);

    public SourceProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    public String insert() {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table());
        StringBuilder columnSql = new StringBuilder(" (");
        StringBuilder propSql = new StringBuilder(" VALUES (");
        for (Field f : getAllFields()) {
            if (!Jpa.insertable(f)) {
                continue;
            }
            columnSql.append(String.format(" %s,", column(f)));
            propSql.append(String.format(" #{%s},", f.getName()));
        }
        columnSql.deleteCharAt(columnSql.length() - 1);
        columnSql.append(" )");
        propSql.deleteCharAt(propSql.length() - 1);
        propSql.append(" )");
        sql.append(columnSql.toString());
        sql.append(propSql.toString());
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 insert, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String updateById() {
        StringBuilder sql = new StringBuilder("UPDATE ").append(table()).append(" SET");
        for (Field f : getAllFields()) {
            if (!Jpa.updatable(f) || f.isAnnotationPresent(Id.class)) {
                continue;
            }
            sql.append(String.format(" %s = #{%s},", column(f), f.getName()));
        }
        sql.deleteCharAt(sql.length() - 1);
        Field id = id();
        sql.append(" WHERE ").append(column(id)).append(String.format(" = #{%s}", id.getName()));
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 updateById, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String getById() {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table()).append(" WHERE ");
        Field id = id();
        sql.append(String.format(" %s = #{%s}", column(id), id.getName()));
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 getById, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String deleteMarkById() {
        Field delete = getDeleteMarkField();
        StringBuilder sql = new StringBuilder("UPDATE ").append(table());
        sql.append(" SET ").append(column(delete)).append(" = ").append(delete.getAnnotation(DeleteMark.class).value());
        sql.append(" WHERE ");
        Field id = id();
        sql.append(String.format("%s = #{%s}", column(id), id.getName()));
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 deleteById, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String deleteById() {
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table()).append(" WHERE ");
        Field id = id();
        sql.append(String.format("%s = #{%s}", column(id), id.getName()));
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 deleteMarkById, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String listAll() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(columnsString());
        sql.append(" FROM ");
        sql.append(table());
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 list, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String countAll() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(0) FROM ");
        sql.append(table());
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 countAll, Source: " + sql.toString());
        }
        return sql.toString();
    }

    Field id() {
        Field id = ProviderContexts.id(clazz);
        if (id == null) {
            throw new MapperException("id不存在");
        }
        return id;
    }


    String columnsString() {
        StringBuilder sql = new StringBuilder();
        for (Field f : getAllFields()) {
            if (!Jpa.selectable(f)) {
                continue;
            }
            sql.append(Jpa.column(f)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length() - 1);
        return sql.toString();
    }

    String table() {
        return Jpa.table(clazz);
    }

    String column(Field f) {
        return Jpa.column(f);
    }

    List<Field> getAllFields() {
        return ClassUtil.getDeclaredFields(clazz);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    Field getDeleteMarkField() {
        Optional<Field> first = ClassUtil.getByAnnotation(DeleteMark.class, getClazz());
        if (!first.isPresent()) {
            throw new MapperException("该注解不存在" + DeleteMark.class.getSimpleName());
        }
        return first.get();
    }
}
