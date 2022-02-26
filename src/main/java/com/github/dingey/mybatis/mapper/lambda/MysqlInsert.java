package com.github.dingey.mybatis.mapper.lambda;

@SuppressWarnings("unused")
public class MysqlInsert<T> extends AbstractInsert<T, MysqlInsert<T>> {
    private String insertType;
    private Update<T> update;

    public MysqlInsert<T> ignore() {
        insertType = "IGNORE";
        return this;
    }

    public MysqlInsert<T> replace() {
        insertType = "REPLACE";
        return this;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        StringBuilder builder = new StringBuilder("<script>");
        builder.append("REPLACE".equals(insertType) ? "REPLACE " : "INSERT ")
                .append("IGNORE".equals(insertType) ? "IGNORE " : "")
                .append("INTO ").append(table());
        appendColumnBuilder(builder);
        appendValueBuilder(builder);
        if (update != null) {
            builder.append(" on duplicate key update ").append(update.toSqlBuilder());
        }
        builder.append("</script>");
        return builder;
    }

    public Update<T> onDuplicateKeyUpdate() {
        Update<T> update = new Update<>();
        update.setParamCount(getParamCount());
        this.update = update;
        return update;
    }

    public class Update<T> extends AbstractSql<T> {
        StringBuilder updateBuilder = new StringBuilder();

        public Update<T> set(SFunction<T, ?> col, Object value) {
            Param param = typedThis.createParam(value);
            if (updateBuilder.length() > 0) {
                updateBuilder.append(",");
            }
            updateBuilder.append(column(col)).append("=").append(param.genELExpression());
            return this;
        }

        @Override
        public StringBuilder toSqlBuilder() {
            return updateBuilder;
        }
    }
}
