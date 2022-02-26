package com.github.dingey.mybatis.mapper.lambda;

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
        return exp(column, val, "%s = #{%s}");
    }

    public Self eq(SFunction<T, ?> column, Object val) {
        return eq(true, column, val);
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
        return exp(column, val, "%s != #{%s}");
    }

    public Self ne(SFunction<T, ?> column, Object val) {
        return eq(true, column, val);
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
        return exp(column, val, "%s &lt; #{%s}");
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
        return exp(column, val, "%s &lt;= #{%s}");
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
        return exp(column, val, "%s &gt; #{%s}");
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
        return exp(column, val, "%s &gt;= #{%s}");
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
        return exp(column, null, "%s IS NULL");
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
        return exp(column, null, "%s IS NOT NULL");
    }

    public Self isNotNull(SFunction<T, ?> column) {
        return exp(column, null, "%s IS NOT NULL");
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
