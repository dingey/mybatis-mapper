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
        return "<script>" +
                "select * from ( select tmp_page.*, rownum row_id from ( " + super.toSql() +
                " ) tmp_page where rownum &lt;= " + rowEnd.genELExpression() + " ) where row_id &gt;" + rowStart.genELExpression() +
                "</script>";
    }
}
