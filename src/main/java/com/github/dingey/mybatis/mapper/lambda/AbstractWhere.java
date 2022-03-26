package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.exception.MapperException;

import java.util.Collection;

@SuppressWarnings({"unused", "unchecked"})
public abstract class AbstractWhere<T, Self extends AbstractWhere<T, Self>> extends AbstractSql<T> {
    StringBuilder whereBuilder = new StringBuilder();
    protected final Self typedThis = (Self) this;

    public Self where() {
        return typedThis;
    }

    /**
     * where条件 col=?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self eq(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return eq(column, val);
    }

    public Self eq(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s = #{%s}");
    }

    /**
     * where条件 col !=?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self ne(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return ne(column, val);
    }

    public Self ne(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s != #{%s}");
    }

    /**
     * <pre>col &lt;= ?</pre>
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self lt(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return lt(column, val);
    }

    public Self lt(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s &lt; #{%s}");
    }

    /**
     * where条件 col &lt;=?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self le(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return le(column, val);
    }

    public Self le(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s &lt;= #{%s}");
    }

    /**
     * where条件 col &gt;=?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self gt(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return gt(column, val);
    }

    public Self gt(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s &gt; #{%s}");
    }

    /**
     * where条件 col &gt;=?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self ge(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return ge(column, val);
    }

    public Self ge(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s &gt;= #{%s}");
    }

    /**
     * where条件 col IS NULL
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @return 自身
     */
    public Self isNull(boolean condition, SFunction<T, ?> column) {
        if (!condition) return typedThis;
        return isNull(column);
    }

    public Self isNull(SFunction<T, ?> column) {
        return exp(column, null, "%s IS NULL");
    }

    /**
     * where条件 col IS NOT NULL
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @return 自身
     */
    public Self isNotNull(boolean condition, SFunction<T, ?> column) {
        if (!condition) return typedThis;
        return isNotNull(column);
    }

    public Self isNotNull(SFunction<T, ?> column) {
        return exp(column, null, "%s IS NOT NULL");
    }

    public Self in(boolean condition, SFunction<T, ?> column, Collection<?> vales) {
        if (!condition) return typedThis;
        return in(column, vales);
    }

    public Self in(SFunction<T, ?> column, Collection<?> vales) {
        if (vales != null && !vales.isEmpty()) {
            return this.in(column, vales.toArray());
        } else {
            throw new MapperException("in值不能为空");
        }
    }

    public Self in(boolean condition, SFunction<T, ?> column, Object... vales) {
        if (!condition) return typedThis;
        return in(column, vales);
    }

    public Self in(SFunction<T, ?> column, Object... vales) {
        if (vales != null && vales.length != 0) {
            String name = column(column);
            Param param = createParam();
            addParam(param.getName(), vales);

            String format1 = String.format("<foreach collection=\"%s\" open=\"%s IN (\" separator=\",\" close=\")\" item=\"id\">#{id}</foreach>", param.genExpression(), name);

            if (whereBuilder.length() > 0) {
                whereBuilder.append(" AND ");
            }
            whereBuilder.append(format1);
            return typedThis;
        } else {
            throw new MapperException("in值不能为空");
        }
    }

    /**
     * where条件 col like ?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param val       值
     * @return 自身
     */
    public Self like(boolean condition, SFunction<T, ?> column, Object val) {
        if (!condition) return typedThis;
        return like(column, val);
    }

    /**
     * where条件 col like ?
     *
     * @param column 列
     * @param val    值
     * @return 自身
     */
    public Self like(SFunction<T, ?> column, Object val) {
        return exp(column, val, "%s LIKE #{%s}");
    }

    /**
     * where条件 col between ? and ?
     *
     * @param condition true拼入参数，false忽略条件
     * @param column    列
     * @param v1        值1
     * @param v2        值2
     * @return 自身
     */
    public Self between(boolean condition, SFunction<T, ?> column, Object v1, Object v2) {
        if (!condition) return typedThis;
        return between(column, v1, v2);
    }

    /**
     * where条件 col between ? and ?
     *
     * @param column 列
     * @param v1     值1
     * @param v2     值2
     * @return 自身
     */
    public Self between(SFunction<T, ?> column, Object v1, Object v2) {
        String name = column(column);
        Param param1 = createParam();
        addParam(param1.getName(), v1);
        Param param2 = createParam();
        addParam(param2.getName(), v2);

        String format1 = String.format("%s BETWEEN #{%s} AND #{%s}", name, param1.genExpression(), param2.genExpression());

        if (whereBuilder.length() > 0) {
            whereBuilder.append(" AND ");
        }
        whereBuilder.append(format1);
        return typedThis;
    }

    protected Self exp(SFunction<T, ?> column, Object val, String format) {
        String name = column(column);
        Param param = createParam();
        addParam(param.getName(), val);

        String format1 = String.format(format, name, param.genExpression());

        if (whereBuilder.length() > 0) {
            whereBuilder.append(" AND ");
        }
        whereBuilder.append(format1);
        return typedThis;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        return whereBuilder;
    }
}
