package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * 通用模板
 *
 * @param <T> 数据模型
 */
@SuppressWarnings("unused")
public interface MybatisMapper<T> {
    /**
     * 插入
     *
     * @param t 参数
     * @return 影响的行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true)
    int insert(T t);

    /**
     * 可选择插入，忽略null
     *
     * @param t 参数
     * @return 影响的行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insertSelective")
    @Options(useGeneratedKeys = true)
    int insertSelective(T t);

    /**
     * 更新一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "update")
    int update(T t);

    /**
     * 更新一条记录，忽略null
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateSelective")
    int updateSelective(T t);

    /**
     * 根据主键删除一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "delete")
    int delete(T t);

    /**
     * 根据主键标记删除一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteMark")
    int deleteMark(T t);

    /**
     * 查询一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "get")
    T get(T t);

    /**
     * 根据主键查询
     *
     * @param t  模型
     * @param id 主键
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "getById")
    T getById(Class<T> t, Serializable id);

    /**
     * 查询列表
     *
     * @param t 参数
     * @return 列表
     */
    @SelectProvider(type = SqlProvider.class, method = "list")
    List<T> list(T t);

    /**
     * 查询总数
     *
     * @param t 参数
     * @return 总数
     */
    @SelectProvider(type = SqlProvider.class, method = "count")
    Integer count(T t);

    /**
     * 查询所有
     *
     * @param t 参数
     */
    @SelectProvider(type = SqlProvider.class, method = "listAll")
    List<T> listAll(Class<T> t);

    /**
     * 汇总所有
     *
     * @param t 参数
     */
    @SelectProvider(type = SqlProvider.class, method = "countAll")
    int countAll(Class<T> t);

    /**
     * 根据主键批量查询
     *
     * @param t   参数
     * @param ids 主键
     */
    @SelectProvider(type = SqlProvider.class, method = "listByIds")
    List<T> listByIds(Class<T> t, Iterable<Integer> ids);
}
