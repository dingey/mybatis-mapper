package com.github.dingey.mybatis.mapper.lambda;

import org.apache.ibatis.jdbc.SQL;

/**
 * @author d
 */
@SuppressWarnings("unused")
public class Delete<T> extends AbstractStatement<T, Delete<T>> {
    public Delete() {
    }

    public Delete(T t) {
        super(t);
    }

    public Delete(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table());
        buildWheres(sql);

        return "<script>" + sql.toString() + "</script>";
    }
}
