package com.github.dingey.mybatis.mapper.lambda;

/**
 * SQL构建器
 */
public interface SqlBuilder {
    StringBuilder toSqlBuilder();

    /**
     * 转换成SQL字符串
     *
     * @return sql
     */
    default String toSql() {
        return toSqlBuilder().toString();
    }
}
