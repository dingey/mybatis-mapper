package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.utils.JpaUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public class AbstractInsert<T, Self extends AbstractInsert<T, Self>> extends AbstractSql<T> {
    protected final Self typedThis = (Self) this;
    private final StringBuilder columnBuilder = new StringBuilder();
    private final StringBuilder valueBuilder = new StringBuilder();
    boolean batch = false;

    /**
     * 设置插入的列，注意要和values的参数个数一直
     *
     * @param functions 列
     * @return 返回自身，方便链式调用
     */
    @SafeVarargs
    public final Self insert(SFunction<T, ?>... functions) {
        if (functions != null && functions.length > 0) {
            for (SFunction<T, ?> function : functions) {
                if (columnBuilder.length() > 0) {
                    columnBuilder.append(",");
                }
                columnBuilder.append(column(function));
            }
        }
        return typedThis;
    }

    /**
     * 设置插入的值，注意要和insert的参数个数一直
     *
     * @param values 值
     * @return 返回自身，方便链式调用
     */
    public Self values(Object... values) {
        if (values != null && values.length > 0) {
            for (Object value : values) {
                Param param = createParam(value);
                if (valueBuilder.length() > 0) {
                    valueBuilder.append(",");
                }
                valueBuilder.append(param.genELExpression());
            }
        }
        return typedThis;
    }

    /**
     * 批量插入，会忽略insert里设置的插入列，插入所有列
     *
     * @param values 实体对象集合
     * @return 返回自身，方便链式调用
     */
    public Self values(Collection<T> values) {
        if (values != null && !values.isEmpty()) {
            batch = true;
            Param param = createParam(values);
            if (valueBuilder.length() > 0) {
                valueBuilder.append(",");
            }
            if (columnBuilder.length() > 0) {
                columnBuilder.delete(0, columnBuilder.length());
            }
            List<Field> fields = JpaUtils.getInsertColumn(getEntityClass());
            valueBuilder.append("<foreach collection =\"").append(param.genExpression()).append("\" item=\"entity\" separator =\",\">(");
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (i != 0) {
                    columnBuilder.append(",");
                    valueBuilder.append(",");
                }
                columnBuilder.append(JpaUtils.column(field));
                valueBuilder.append(String.format(" #{entity.%s}", field.getName()));
            }
            valueBuilder.append(")</foreach>");
        }
        return typedThis;
    }

    /**
     * 设置插入的表对象
     *
     * @param tClass 插入的表对象
     * @return 返回自身，方便链式调用
     */
    public Self into(Class<T> tClass) {
        setEntityClass(tClass);
        return typedThis;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        StringBuilder builder = new StringBuilder();
        builder.append("<script>INSERT INTO ").append(table());

        appendColumnBuilder(builder);
        appendValueBuilder(builder);

        builder.append("</script>");
        return builder;
    }

    protected StringBuilder getColumnBuilder() {
        return columnBuilder;
    }

    protected StringBuilder getValueBuilder() {
        return valueBuilder;
    }

    protected void appendColumnBuilder(StringBuilder builder) {
        if (columnBuilder.length() > 0) {
            builder.append("(").append(columnBuilder).append(")");
        } else {
            builder.append(JpaUtils.getColumnString(getEntityClass()));
        }
    }

    protected void appendValueBuilder(StringBuilder builder) {
        if (valueBuilder.length() > 0) {
            builder.append(" VALUES ").append(batch ? "" : "(").append(valueBuilder).append(batch ? "" : ")");
        }
    }
}
