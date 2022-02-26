package com.github.dingey.mybatis.mapper.lambda;

@SuppressWarnings("unused")
public class MysqlSelect<T> extends AbstractSelect<T, MysqlSelect<T>> {
    private String limit;

    public MysqlSelect<T> limit(int size) {
        Param p = createParam(size);
        limit = String.format(" LIMIT %s", p.genELExpression());
        return this;
    }

    /**
     * MYSQL分页参数
     *
     * @param offset 开始
     * @param size   大小
     * @return 自身
     */
    public MysqlSelect<T> limit(long offset, int size) {
        Param p1 = createParam(offset);
        Param p2 = createParam(size);
        limit = String.format(" LIMIT %s,%s", p1.genELExpression(), p2.genELExpression());
        return this;
    }

    @Override
    public String toSql() {
        return "<script>" + super.toSql() + (limit == null ? "" : limit) + "</script>";
    }
}
