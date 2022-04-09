package com.github.dingey.mybatis.mapper.lambda;

@SuppressWarnings("unused")
public class Update<T> extends AbstractWhere<T, Update<T>> {
    private final StringBuilder builder = new StringBuilder();
    private final StringBuilder setBuilder = new StringBuilder();
    private final Update<T> instance;

    public Update() {
        this.instance = this;
    }

    public Update<T> update(Class<T> tClass) {
        setEntityClass(tClass);
        return instance;
    }

    /**
     * 创建带列可计算的值，支持 + - * /
     */
    public ColumnValue<T> val(SFunction<T, ?> col) {
        return new ColumnValue<>(col, instance.getParamCount(), getParams());
    }

    public ColumnValue<T> val(Object v) {
        return new ColumnValue<>(v, instance.getParamCount(), getParams());
    }

    /**
     * 设置需要更新的列及值
     *
     * @param col   列
     * @param value 值
     * @return 自身
     */
    public Update<T> set(SFunction<T, ?> col, Object value) {
        appendSetPrefix();

        Param param = createParam();
        addParam(param.getName(), value);

        setBuilder.append(column(col)).append("=").append(param.genELExpression());
        return this;
    }

    /**
     * 设置需要更新的列及 带函数的值，如自增等
     *
     * @param col   列
     * @param value 值
     * @return 自身
     */
    public Update<T> set(SFunction<T, ?> col, ColumnValue<T> value) {
        appendSetPrefix();

        setBuilder.append(column(col)).append("=").append(value.toSqlBuilder());
        return this;
    }

    private void appendSetPrefix() {
        if (setBuilder.length() > 0) {
            setBuilder.append(",");
        }
    }

    public Update<T> where() {
        return this;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        builder.append("<script>UPDATE ").append(instance.table()).append(" ");

        if (setBuilder.length() > 0) {
            builder.append("SET ").append(setBuilder);
        }
        if (whereBuilder.length() > 0) {
            builder.append(" WHERE ").append(whereBuilder);
        }
        return builder.append("</script>");
    }
}
