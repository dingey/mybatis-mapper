package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.exception.MapperException;
import com.github.dingey.mybatis.mapper.utils.JpaUtils;
import com.github.dingey.mybatis.mapper.utils.LambdaUtils;

import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author d
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class AbstractStatement<T, Children extends AbstractStatement<T, Children>> {
    private Map<String, Object> params;
    private final StringBuilder where = new StringBuilder();
    AtomicInteger paramCount = new AtomicInteger(0);
    protected final Children typedThis = (Children) this;
    T entity;
    Class<T> entityClass;

    public AbstractStatement() {
        this((T) null);
    }

    public AbstractStatement(T t) {
        this.entity = t;
    }

    public AbstractStatement(Class<T> clazz) {
        this.entityClass = clazz;
        params = new LinkedHashMap<>();
    }

    public abstract String toSql();

    Children addParam(String paramName, Object paramValue) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(paramName, paramValue);
        return typedThis;
    }

    Children setEntity(T entity) {
        this.entity = entity;
        return typedThis;
    }

    public Class<T> getEntityClass() {
        if (entityClass == null && entity != null) {
            entityClass = (Class<T>) entity.getClass();
        }
        return entityClass;
    }

    public Children setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
        return typedThis;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    String column(SFunction<T, ?> column) {
        SerializedLambda lambda = LambdaUtils.getSerializedLambda(column);
        if (entityClass == null) {
            entityClass = (Class<T>) LambdaUtils.getLambdaClass(lambda);
        }
        return JpaUtils.column(lambda);
    }

    public Children eq(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("%s = #{%s}", name, elPropName);
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children lt(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("%s &lt; #{%s}", name, elPropName);
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children le(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("%s &lt;= #{%s}", name, elPropName);
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children gt(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("%s &gt; #{%s}", name, elPropName);
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children ge(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("%s &gt;= #{%s}", name, elPropName);
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children isNull(SFunction<T, ?> column) {
        String name = column(column);

        String format = String.format("%s IS NULL", name);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children isNotNull(SFunction<T, ?> column) {
        String name = column(column);

        String format = String.format("%s IS NOT NULL", name);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children in(SFunction<T, ?> column, Collection<?> vales) {
        if (vales == null || vales.isEmpty()) {
            throw new MapperException("in值不能为空");
        }
        return in(column, vales.toArray());
    }

    public Children in(SFunction<T, ?> column, Object... vales) {
        if (vales == null || vales.length == 0) {
            throw new MapperException("in值不能为空");
        }
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("<foreach collection=\"%s\" open=\"%s IN (\" separator=\",\" close=\")\" item=\"id\">#{id}</foreach>", elPropName, name);
        addParam(propName, vales);
        where.append(format).append(" AND ");

        return typedThis;
    }

    public Children notIn(SFunction<T, ?> column, Collection<?> vales) {
        if (vales == null || vales.isEmpty()) {
            throw new MapperException("in值不能为空");
        }
        return notIn(column, vales.toArray());
    }

    public Children notIn(SFunction<T, ?> column, Object... vales) {
        if (vales == null || vales.length == 0) {
            throw new MapperException("in值不能为空");
        }
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = String.format("<foreach collection=\"%s\" open=\"%s NOT IN (\" separator=\",\" close=\")\" item=\"id\">#{id}</foreach>", elPropName, name);
        addParam(propName, vales);
        where.append(format).append(" AND ");

        return typedThis;
    }

    /**
     * LIKE '%值%'
     */
    public Children like(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = column + " LIKE CONCAT('%',#{" + elPropName + "},'%')";
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children notLike(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = column + " NOT LIKE CONCAT('%',#{" + elPropName + "},'%')";
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    /**
     * LIKE '%值'
     */
    public Children likeLeft(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = column + " LIKE CONCAT('%',#{" + elPropName + "})";
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    /**
     * LIKE '值%'
     */
    public Children likeRight(SFunction<T, ?> column, Object val) {
        String name = column(column);
        String propName = getPropName(name);
        String elPropName = getELPropName(propName);

        String format = column + " LIKE CONCAT(#{" + elPropName + "},'%')";
        addParam(propName, val);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children between(SFunction<T, ?> column, Object val1, Object val2) {
        String name = column(column);
        String propName1 = getPropName(name);
        String elPropName1 = getELPropName(propName1);

        String propName2 = getPropName(name);
        String elPropName2 = getELPropName(propName2);

        String format = String.format("%s BETWEEN #{%s} AND #{%s}", name, elPropName1, elPropName2);
        addParam(propName1, val1);
        addParam(propName2, val2);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public Children notBetween(SFunction<T, ?> column, Object val1, Object val2) {
        String name = column(column);
        String propName1 = getPropName(name);
        String elPropName1 = getELPropName(propName1);

        String propName2 = getPropName(name);
        String elPropName2 = getELPropName(propName2);

        String format = String.format("%s NOT BETWEEN #{%s} AND #{%s}", name, elPropName1, elPropName2);
        addParam(propName1, val1);
        addParam(propName2, val2);
        where.append(format).append(" AND ");
        return typedThis;
    }

    public String table() {
        return JpaUtils.table(getEntityClass());
    }

    private String getPropName(String column) {
        return column + paramCount.getAndIncrement();
    }

    private String getELPropName(String propName) {
        return "s.params." + propName;
    }

    void buildWheres(StringBuilder sql) {
        String where = getWhere();
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(where);
        }
    }

    String getWhere() {
        String s = where.toString();
        if (s.endsWith(" AND ")) {
            s = s.substring(0, s.length() - 5);
        }
        return s;
    }
}
