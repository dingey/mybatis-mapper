package com.github.dingey.mybatis.mapper.core;

import com.github.dingey.mybatis.mapper.annotation.DeleteMark;
import com.github.dingey.mybatis.mapper.utils.ClassUtils;
import com.github.dingey.mybatis.mapper.utils.JpaUtils;

import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author d
 * @since 0.4.0
 */
@SuppressWarnings("unused")
class SourceScriptProvider<T> extends SourceProvider<T> {

    public SourceScriptProvider(Class<T> clazz) {
        super(clazz);
    }

    public String insertSelective() {
        StringBuilder sql = new StringBuilder("<script>INSERT INTO ").append(table());
        sql.append(trim1());
        sql.append(trim2());
        sql.append("</script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 insertSelective, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String insertBatch() {
        StringBuilder sql = new StringBuilder("<script>INSERT INTO ").append(table());
        StringBuilder columnSql = new StringBuilder(" (");
        StringBuilder propSql = new StringBuilder(" (");
        for (Field f : getAllFields()) {
            if (!JpaUtils.insertable(f)) {
                continue;
            }
            columnSql.append(String.format(" %s,", column(f)));
            propSql.append(String.format(" #{entity.%s},", f.getName()));
        }
        columnSql.deleteCharAt(columnSql.length() - 1);
        columnSql.append(" )");
        propSql.deleteCharAt(propSql.length() - 1);
        propSql.append(" )");
        sql.append(columnSql.toString());
        sql.append(" VALUES<foreach collection =\"list\" item=\"entity\" separator =\",\">");
        sql.append(propSql.toString());
        sql.append("</foreach></script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 insert, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String updateByIdSelective() {
        StringBuilder sql = new StringBuilder("<script>UPDATE ").append(table());
        sql.append(set());
        sql.append(" WHERE ");
        sql.append(idExp());
        sql.append("</script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 updateByIdSelective, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String updates() {
        StringBuilder sql = new StringBuilder("<script>UPDATE ").append(table());
        sql.append(set("columns."));
        sql.append(where("cond."));
        sql.append("</script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 updates, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String listByIds() {
        StringBuilder sql = new StringBuilder("<script>SELECT ").append(columnsString()).append(" FROM ").append(table()).append(" WHERE ");
        sql.append(String.format("%s IN ", column(id())));
        sql.append("<foreach item=\"id\" collection=\"ids\" separator=\",\" open=\"(\" close=\")\" index=\"\">#{id}</foreach></script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 listByIds, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String deleteByIds() {
        StringBuilder sql = new StringBuilder("<script>DELETE FROM ").append(table()).append(" WHERE ");
        sql.append(String.format("%s IN ", column(id())));
        sql.append("<foreach item=\"id\" collection=\"ids\" separator=\",\" open=\"(\" close=\")\" index=\"\">#{id}</foreach></script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 deleteByIds, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String deleteMarkByIds() {
        Field delete = getDeleteMarkField();
        StringBuilder sql = new StringBuilder("<script>UPDATE ").append(table()).append(" SET ");
        sql.append(JpaUtils.column(delete)).append(" = ").append(delete.getAnnotation(DeleteMark.class).value());
        sql.append(String.format(" WHERE %s IN ", column(id())));
        sql.append("<foreach item=\"id\" collection=\"ids\" separator=\",\" open=\"(\" close=\")\" index=\"\">#{id}</foreach></script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 deleteMarkByIds, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String list() {
        StringBuilder sql = new StringBuilder("<script>");
        sql.append("SELECT ");
        sql.append(columnsString());
        sql.append(" FROM ");
        sql.append(table());
        sql.append(where());
        sql.append(order());
        sql.append("</script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 list, Source: " + sql.toString());
        }
        return sql.toString();
    }

    public String count() {
        StringBuilder sql = new StringBuilder("<script>");
        sql.append("SELECT COUNT(0) FROM ");
        sql.append(table());
        sql.append(where());
        sql.append("</script>");
        if (log.isDebugEnabled()) {
            log.debug("==>  方法 count, Source: " + sql.toString());
        }
        return sql.toString();
    }

    private String trim1() {
        StringBuilder sql = new StringBuilder(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (Field f : getAllFields()) {
            if (!JpaUtils.insertable(f)) {
                continue;
            }
            sql.append(String.format("<if test=\"%s != null\">%s,</if>", f.getName(), JpaUtils.column(f)));
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private String trim2() {
        StringBuilder sql = new StringBuilder(" <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
        for (Field f : getAllFields()) {
            if (!JpaUtils.insertable(f)) {
                continue;
            }
            sql.append(String.format("<if test=\"%s != null\">#{%s},</if>", f.getName(), f.getName()));
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private String set() {
        return set("");
    }

    private String set(String param) {
        StringBuilder sql = new StringBuilder("<set>");
        for (Field f : getAllFields()) {
            if (!JpaUtils.updatable(f) || f.isAnnotationPresent(Id.class)) {
                continue;
            }
            sql.append(String.format("<if test=\"%s%s !=null\"> %s=#{%s%s},</if>", param, f.getName(), JpaUtils.column(f), param, f.getName()));
        }
        sql.append("</set>");
        return sql.toString();
    }

    private String idExp() {
        return String.format("%s=#{%s}", JpaUtils.column(id()), id().getName());
    }

    private String where() {
        return where("");
    }

    private String where(String param) {
        StringBuilder sql = new StringBuilder("<where>");
        for (Field f : getAllFields()) {
            if (JpaUtils.selectable(f)) {
                sql.append(String.format("<if test=\"%s%s !=null\"> and %s=#{%s%s}</if>", param, f.getName(), JpaUtils.column(f), param, f.getName()));
            }
        }
        sql.append("</where>");
        return sql.toString();
    }

    private String order() {
        Optional<Field> optional = ClassUtils.getByAnnotation(OrderBy.class, getClazz());
        if (!optional.isPresent()) {
            return "";
        }
        Field f = optional.get();
        OrderBy orderBy = f.getAnnotation(OrderBy.class);
        if (!orderBy.value().isEmpty()) {
            return String.format(" order by %s", orderBy.value());
        } else if (f.isAnnotationPresent(Transient.class)) {
            return String.format("<if test=\"%s !=null\"> order by #{%s} </if>", f.getName(), f.getName());
        } else {
            return String.format(" order by %s", JpaUtils.column(f));
        }
    }
}
