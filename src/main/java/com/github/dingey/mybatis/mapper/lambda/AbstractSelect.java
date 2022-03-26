package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.utils.JpaUtils;

@SuppressWarnings("unused")
public class AbstractSelect<T, Self extends AbstractSelect<T, Self>> extends AbstractWhere<T, Self> {
    private final StringBuilder columnBuilder = new StringBuilder();
    private final StringBuilder groupBuilder = new StringBuilder();
    private final StringBuilder havingBuilder = new StringBuilder();
    private final StringBuilder orderBuilder = new StringBuilder();
    //private final Select<T> instance;

    public AbstractSelect() {
        //this.instance = this;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (columnBuilder.length() > 0) {
            builder.append(columnBuilder);
        } else {
            builder.append(JpaUtils.getColumnString(getEntityClass()));
        }
        builder.append(" FROM ").append(table());

        if (whereBuilder.length() > 0) {
            builder.append(" WHERE ").append(whereBuilder);
        }

        if (groupBuilder.length() > 0) {
            builder.append(groupBuilder);
        }

        if (havingBuilder.length() > 0) {
            builder.append(havingBuilder);
        }

        if (orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }
        return builder;
    }

    /**
     * 设置查询的列
     *
     * @param cols 列
     * @return 返回自身，方便链式调用
     */
    @SafeVarargs
    public final Self select(SFunction<T, ?>... cols) {
        if (cols != null) {
            for (SFunction<T, ?> col : cols) {
                if (columnBuilder.length() > 0) {
                    columnBuilder.append(",");
                }
                columnBuilder.append(column(col));
            }
        }
        return typedThis;
    }

    /**
     * 设置查询的列，可以对列做特殊操作，如函数及简单加减操作
     *
     * @param col 列
     * @return 返回自身，方便链式调用
     */
    public Self select(ColumnValue<T> col) {
        if (columnBuilder.length() > 0) {
            columnBuilder.append(",");
        }
        columnBuilder.append(col.toSqlBuilder());
        return typedThis;
    }
//
//    /**
//     * 设置查询的列，可以对列做特殊操作，如函数及简单加减操作
//     *
//     * @param col 列
//     * @return 返回自身，方便链式调用
//     */
//    public Self select(Func<T> col) {
//        if (columnBuilder.length() > 0) {
//            columnBuilder.append(",");
//        }
//        columnBuilder.append(col.toSql());
//        return typedThis;
//    }

    public Self from(Class<T> tClass) {
        setEntityClass(tClass);
        return typedThis;
    }

    /**
     * 设置分组的列
     *
     * @param function 列
     * @return 返回自身，方便链式调用
     */
    public Self groupBy(SFunction<T, ?> function) {
        groupBuilder.append(" GROUP BY ").append(column(function));
        return typedThis;
    }

    /**
     * 设置分组后的条件
     *
     * @param columnValue 包含表达式
     * @return 返回自身，方便链式调用
     */
    public Self having(ColumnValue<T> columnValue) {
        havingBuilder.append(" HAVING ").append(columnValue.toSqlBuilder());
        return typedThis;
    }

    /**
     * 设置排序列
     *
     * @param function 排序列
     * @return 返回自身，方便链式调用
     */
    public Self orderBy(SFunction<T, ?> function) {
        if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
        orderBuilder.append(column(function));
        return typedThis;
    }

    /**
     * 设置排序列递增
     *
     * @param function 排序列
     * @return 返回自身，方便链式调用
     */
    public Self orderByAsc(SFunction<T, ?> function) {
        if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
        orderBuilder.append(column(function)).append(" ASC");
        return typedThis;
    }

    /**
     * 设置排序列递减
     *
     * @param function 排序列
     * @return 返回自身，方便链式调用
     */
    public Self orderByDesc(SFunction<T, ?> function) {
        if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
        orderBuilder.append(column(function)).append(" DESC");
        return typedThis;
    }

    /**
     * 创建带列的值
     *
     * @param f 列
     * @return 列值
     */
    public ColumnValue<T> col(SFunction<T, ?> f) {
        return new ColumnValue<>(f, getParamCount(), getParams());
    }

    /**
     * 创建带列的值
     *
     * @param v 列
     * @return 列值
     */
    public ColumnValue<T> col(Number v) {
        return new ColumnValue<>(v, getParamCount(), getParams());
    }

    /**
     * 创建 SUM列
     *
     * @param function 列
     * @return 列值
     */
    public ColumnValue<T> sum(SFunction<T, ?> function) {
        return new ColumnValue<>(new Func<>(function, Func.FuncEnum.SUM), getParamCount(), getParams());
    }


    /**
     * 创建 MIN列
     *
     * @param function 列
     * @return 列值
     */
    public ColumnValue<T> min(SFunction<T, ?> function) {
        return new ColumnValue<>(new Func<>(function, Func.FuncEnum.MIN), getParamCount(), getParams());
    }

    /**
     * 创建 MAX列
     *
     * @param function 列
     * @return 列值
     */
    public ColumnValue<T> max(SFunction<T, ?> function) {
        return new ColumnValue<>(new Func<>(function, Func.FuncEnum.MAX), getParamCount(), getParams());
    }

    /**
     * 创建 AVG列
     *
     * @param function 列
     * @return 列值
     */
    public ColumnValue<T> avg(SFunction<T, ?> function) {
        return new ColumnValue<>(new Func<>(function, Func.FuncEnum.AVG), getParamCount(), getParams());
    }

    /**
     * 创建 COUNT列
     *
     * @param function 列
     * @return 列值
     */
    public ColumnValue<T> count(SFunction<T, ?> function) {
        return new ColumnValue<>(new Func<>(function, Func.FuncEnum.COUNT), getParamCount(), getParams());
    }
}
