package com.github.dingey.mybatis.mapper.lambda;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "unchecked"})
public class ColumnValue<T> extends AbstractSql<T> {
    StringBuilder builder = new StringBuilder();

    protected ColumnValue(Object v, AtomicInteger paramCount, Map<String, Object> params) {
        setParamCount(paramCount);
        setParams(params);
        this.op(v, null);
    }

    protected ColumnValue(Func<T> func, AtomicInteger paramCount, Map<String, Object> params) {
        setParamCount(paramCount);
        setParams(params);
        this.op(func, null, false);
    }

    public ColumnValue<T> plus(Number number) {
        return op(number, "+");
    }

    public ColumnValue<T> plus(ColumnValue<T> v) {
        return op(v, "+");
    }

    public ColumnValue<T> plus(SFunction<T, ?> f) {
        return op(f, "+");
    }

    public ColumnValue<T> minus(Number number) {
        return op(number, "-");
    }

    public ColumnValue<T> minus(ColumnValue<T> v) {
        return op(v, "-");
    }

    public ColumnValue<T> minus(SFunction<T, ?> f) {
        return op(f, "-");
    }

    public ColumnValue<T> multiply(Number number) {
        return op(number, "*");
    }

    public ColumnValue<T> multiply(ColumnValue<T> number) {
        return op(number, "*");
    }

    public ColumnValue<T> multiply(SFunction<T, ?> f) {
        return op(f, "*");
    }

    public ColumnValue<T> divide(Number number) {
        return op(number, "/");
    }

    public ColumnValue<T> divide(ColumnValue<T> number) {
        return op(number, "/");
    }

    public ColumnValue<T> divide(SFunction<T, ?> f) {
        return op(f, "/");
    }

    public ColumnValue<T> gt(Number v) {
        return op(v, "&gt;");
    }

    public ColumnValue<T> ge(Number v) {
        return op(v, "&gt;=");
    }

    public ColumnValue<T> eq(Number v) {
        return op(v, "&gt;");
    }

    public ColumnValue<T> lt(Number v) {
        return op(v, "&lt;");
    }

    public ColumnValue<T> le(Number v) {
        return op(v, "&lt;=");
    }

    private ColumnValue<T> op(Object v, String op) {
        return op(v, op, true);
    }

    private ColumnValue<T> op(Object v, String op, boolean quote) {
        if (op != null) {
            builder.append(op);
        }
        if (v instanceof Number) {
            Param param = createParam();
            addParam(param.getName(), v);
            builder.append(param.genELExpression());
        } else if (v instanceof SFunction) {
            builder.append(column((SFunction<T, ?>) v));
        } else if (v instanceof SqlBuilder) {
            builder.append(quote ? "(" : "").append(((SqlBuilder) v).toSql()).append(quote ? ")" : "");
        }
        return this;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        return builder;
    }
}
