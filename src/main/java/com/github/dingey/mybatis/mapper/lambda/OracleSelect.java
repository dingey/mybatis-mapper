package com.github.dingey.mybatis.mapper.lambda;

@SuppressWarnings("unused")
public class OracleSelect<T> extends AbstractSelect<T, OracleSelect<T>> {
    private Param rowStart;
    private Param rowEnd;

    /**
     * 分页查询，如查询1页，每页5，则传0，5
     */
    public OracleSelect<T> rowNum(long offset, int size) {
        rowStart = createParam(offset);
        rowEnd = createParam(offset + size);

        return this;
    }

    @Override
    public String toSql() {
        if (rowStart == null) {
            return "<script>" + super.toSql() + "</script>";
        }
        StringBuilder sql = new StringBuilder("<script>");
        sql.append("select * from ( select tmp_page.*, rownum row_id from ( ").append(super.toSql());
        sql.append(" ) tmp_page where rownum &lt;= ").append(rowEnd.genELExpression()).append(" ) where row_id &gt;").append(rowStart.genELExpression());
        sql.append("</script>");
        return sql.toString();
    }
}
