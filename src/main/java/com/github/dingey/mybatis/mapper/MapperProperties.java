package com.github.dingey.mybatis.mapper;

/**
 * @author d
 */
public interface MapperProperties {
    /**
     * 是否驼峰转下划线
     *
     * @return true/false
     */
    boolean isCamelCase();

    /**
     * 表前缀
     *
     * @return 表前缀
     */
    String getTablePrefix();

    /**
     * 是否表名大写
     *
     * @return true/false
     */
    boolean isTableUpper();

    /**
     * 是否列名大写
     *
     * @return true/false
     */
    boolean isColumnUpper();
}
