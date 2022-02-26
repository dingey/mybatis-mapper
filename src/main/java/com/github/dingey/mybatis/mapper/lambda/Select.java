package com.github.dingey.mybatis.mapper.lambda;

public class Select<T> extends AbstractSelect<T, Select<T>> {

    @Override
    public String toSql() {
        return "<script>" + super.toSql() + "</script>";
    }
}
