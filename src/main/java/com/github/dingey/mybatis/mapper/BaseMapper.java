package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * 通用模板
 *
 * @param <T> 数据模型
 * @author d
 */
@SuppressWarnings("unused")
public interface BaseMapper<T> {
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
     * 根据主键更新一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateById")
    int updateById(T t);

    /**
     * 根据主键更新一条记录，忽略null
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(T t);

    /**
     * 根据主键删除一条记录，如果有DeleteMark则假删除，否则真删除
     *
     * @param id 主键
     * @return 影响的行数
     * @see DeleteMark
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteSmartById")
    int deleteById(Serializable id);

    /**
     * 根据主键删除一条记录，如果有DeleteMark则假删除，否则真删除
     *
     * @param ids 主键集合
     * @return 影响的行数
     * @see DeleteMark
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteSmartByIds")
    int deleteByIds(@Param("ids") Iterable<Serializable> ids);

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 一条记录
     */
    @SelectProvider(type = SqlProvider.class, method = "getById")
    T getById(Serializable id);

    /**
     * 查询一条记录
     *
     * @param t 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "list")
    T get(T t);

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
    long count(T t);

    /**
     * 查询所有
     *
     * @return 列表
     */
    @SelectProvider(type = SqlProvider.class, method = "listAll")
    List<T> listAll();

    /**
     * 汇总所有
     *
     * @return 总数
     */
    @SelectProvider(type = SqlProvider.class, method = "countAll")
    long countAll();

    /**
     * 根据主键批量查询
     *
     * @param ids 主键
     * @return 列表
     */
    @SelectProvider(type = SqlProvider.class, method = "listByIds")
    List<T> listByIds(@Param("ids") Iterable<Serializable> ids);
}
