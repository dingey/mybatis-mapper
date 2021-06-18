package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.utils.JpaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ding
 * @since 2021/4/19
 */
@SuppressWarnings("unused")
public class Select<T> extends AbstractStatement<T, Select<T>> {
    private List<String> columns;
    private String countColumn;
    private String lastSql;

    public Select() {
        this((T) null);
    }

    public Select(T t) {
        super.setEntity(t);
    }

    public Select(Class<T> clazz) {
        super.setEntityClass(clazz);
    }

    @SafeVarargs
    public final Select<T> select(SFunction<T, ?>... columns) {
        Objects.requireNonNull(columns, "columns can't null");
        if (columns != null && columns.length > 0) {
            this.columns = new ArrayList<>(columns.length);
            for (SFunction<T, ?> column : columns) {
                this.columns.add(column(column));
            }
        }

        return this;
    }

    public Select<T> from(Class<T> tClass) {
        setEntityClass(tClass);
        return this;
    }

    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder("SELECT ");

        buildColumns(sql);
        sql.append(" FROM ").append(table());
        buildWheres(sql);

        if (orderBys != null && !orderBys.isEmpty()) {
            sql.append(" ORDER BY ");
            for (String orderBy : orderBys) {
                sql.append(orderBy).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        if (lastSql != null && !lastSql.isEmpty()) {
            sql.append(" ").append(lastSql);
        }

        return "<script>" + sql.toString() + "</script>";
    }

    public String toCountSql() {
        StringBuilder sql = new StringBuilder("SELECT ");

        if (countColumn != null) {
            sql.append("COUNT(" + countColumn + ")");
        } else {
            sql.append("COUNT(0)");
        }
        sql.append(" FROM ").append(table());
        buildWheres(sql);

        return "<script>" + sql.toString() + "</script>";
    }

    private void buildColumns(StringBuilder sql) {
        if (columns != null && !columns.isEmpty()) {
            for (String column : columns) {
                sql.append(column).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        } else {
            sql.append(JpaUtils.getColumnString(getEntityClass()));
        }
    }

    public Select<T> count(SFunction<T, ?> column) {
        this.countColumn = column(column);
        return this;
    }

    public Select<T> count(String column) {
        this.countColumn = column;
        return this;
    }

    /**
     * 拼接到 sql 的最后
     * <p>例: {@code last("limit 1")}</p>
     *
     * @param lastSql 拼接的sql内容
     * @return 查询参数
     */
    public Select<T> last(String lastSql) {
        this.lastSql = lastSql;
        return this;
    }

    public String countColumn() {
        return countColumn;
    }

    private List<String> orderBys;

    @SafeVarargs
    public final Select<T> orderBy(SFunction<T, ?>... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (orderBys == null) {
            orderBys = new ArrayList<>();
        }
        for (SFunction<T, ?> column : columns) {
            String col = column(column);
            orderBys.add(col);
        }

        return this;
    }

    @SafeVarargs
    public final Select<T> orderByAsc(SFunction<T, ?>... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (orderBys == null) {
            orderBys = new ArrayList<>();
        }

        for (SFunction<T, ?> column : columns) {
            String col = column(column);
            orderBys.add(String.format("%s ASC", col));
        }

        return this;
    }

    @SafeVarargs
    public final Select<T> orderByDesc(SFunction<T, ?>... columns) {
        if (columns == null || columns.length == 0) {
            return this;
        }
        if (orderBys == null) {
            orderBys = new ArrayList<>();
        }

        for (SFunction<T, ?> column : columns) {
            String col = column(column);
            orderBys.add(String.format("%s DESC", col));
        }

        return this;
    }
}
