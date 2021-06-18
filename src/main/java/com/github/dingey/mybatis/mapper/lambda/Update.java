package com.github.dingey.mybatis.mapper.lambda;

import org.apache.ibatis.jdbc.SQL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ding
 * @since 2021/4/19
 */
@SuppressWarnings("unused")
public class Update<T> extends AbstractStatement<T, Update<T>> {
    public Update() {
    }

    public Update(T t) {
        super(t);
    }

    public Update(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(table());
        buildSets(sql);
        buildWheres(sql);

        return "<script>" + sql.toString() + "</script>";
    }

    private void buildSets(StringBuilder sql) {
        if (!setsMap.isEmpty()) {
            sql.append(" SET ");
            for (Map.Entry<String, String> set : setsMap.entrySet()) {
                sql.append(String.format("%s=#{s.params.%s},", set.getKey(), set.getValue()));
            }
            sql.deleteCharAt(sql.length() - 1);
        }
    }

    Map<String, String> setsMap = new HashMap<>();

    public Update<T> set(SFunction<T, ?> column, Object val) {
        String col = column(column);
        String propName = col + paramCount.get();
        addParam(propName, val);
        setsMap.put(col, propName);

        return this;
    }
}
