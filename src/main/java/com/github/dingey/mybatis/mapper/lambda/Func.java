package com.github.dingey.mybatis.mapper.lambda;

import java.util.Objects;

@SuppressWarnings("unused")
final class Func<T> extends AbstractSql<T> implements SqlBuilder {
    private final SFunction<T, ?> f;
    private final FuncEnum funcEnum;

    protected Func(SFunction<T, ?> f, FuncEnum funcEnum) {
        Objects.requireNonNull(f);
        Objects.requireNonNull(funcEnum);
        this.f = f;
        this.funcEnum = funcEnum;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toSql() {
        return String.format("%s(%s)", funcEnum.name(), column(f));
    }

    enum FuncEnum {
        SUM, MIN, MAX, AVG, COUNT,
        ;
    }
}
