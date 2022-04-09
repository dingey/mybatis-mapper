package com.github.dingey.mybatis.mapper.lambda;

@SuppressWarnings("unused")
public class Delete<T> extends AbstractWhere<T, Delete<T>> {

    public Delete<T> from(Class<T> tClass) {
        setEntityClass(tClass);
        return typedThis;
    }

    @Override
    public StringBuilder toSqlBuilder() {
        StringBuilder builder = new StringBuilder("<script>DELETE FROM ").append(table());
        if (whereBuilder.length() > 0) {
            builder.append(" WHERE ").append(whereBuilder);
        }
        return builder.append("</script>");
    }
}
