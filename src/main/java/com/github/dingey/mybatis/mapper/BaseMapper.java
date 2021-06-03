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
     * 插入，包含null的字段。生成的sql:
     * <p>{@code insert into t ( col1 ) values ( #{col1} )}
     *
     * @param t 参数
     * @return 影响的行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true)
    int insert(T t);

    /**
     * 可选择插入，忽略null的字段。
     * <p>生成的sql:
     * <p><pre>{@code insert into t
     * <trim prefix="(" suffix=")" suffixOverrides=",">
     *     <if test="nickName != null">
     *       col1,
     *     </if>
     * </trim>
     * <trim prefix="values (" suffix=")" suffixOverrides=",">
     *     <if test="col1 != null">
     *       #{col1},
     *     </if>
     * </trim>}
     * </pre>
     *
     * @param t 参数
     * @return 影响的行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insertSelective")
    @Options(useGeneratedKeys = true)
    int insertSelective(T t);

    /**
     * 根据主键更新一条记录，忽略null的字段，等同于{@link BaseMapper#updateByIdSelective}。
     * <p>生成的sql:
     * <p><pre>{@code update t
     * <set>
     *     <if test="col1 != null">
     *       col1 = #{col1},
     *     </if>
     * </set>
     * where id = #{id} }
     * </pre>
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateByIdSelective")
    int update(T t);

    /**
     * 根据主键更新一条记录，包含null的字段。
     * <p>生成的sql:
     * <p><pre>{@code update t
     * set col1 = #{col1}
     * where id = #{id} }
     * </pre>
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateById")
    int updateById(T t);

    /**
     * 根据主键更新一条记录，忽略null的字段,等同于{@link BaseMapper#updateByIdSelective}
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateByIdSelective")
    int updateSelective(T t);

    /**
     * 根据主键更新一条记录，忽略null的字段,等同于{@link BaseMapper#updateSelective}
     *
     * @param t 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(T t);

    /**
     * 根据条件批量更新数据，忽略null的字段
     *
     * @param columns   需要更新的列，忽略null的字段
     * @param condition where后的条件，忽略null的字段
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "updates")
    int updates(@Param("columns") T columns, @Param("cond") T condition);

    /**
     * 根据主键删除一条记录，如果有{@code DeleteMark}则假删除，否则真删除
     *
     * @param id 主键
     * @return 影响的行数
     * @see DeleteMark
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteSmartById")
    int deleteById(Serializable id);

    /**
     * 根据主键删除一条记录，如果有DeleteMark则假删除，否则真删除。如果需要更新修改时间等，推荐用 {@link BaseMapper#update}方法
     *
     * @param ids 主键集合
     * @return 影响的行数
     * @see DeleteMark
     */
    @UpdateProvider(type = SqlProvider.class, method = "deleteSmartByIds")
    int deleteByIds(@Param("ids") Iterable<? extends Serializable> ids);

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 一条记录
     */
    @SelectProvider(type = SqlProvider.class, method = "getById")
    T getById(Serializable id);

    /**
     * 查询一条记录，结果多于一条会报错
     *
     * @param t 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "list")
    T get(T t);

    /**
     * 查询列表，所有不为null的字段相等为条件。
     * <p>生成的sql:
     * <p><pre>{@code select col1,col2... from t
     * <where>
     *     <if test="col1 != null">
     *       and col1 = #{col1}
     *     </if>
     *     ...
     * </where> }
     * </pre>
     *
     * @param t 参数
     * @return 列表
     */
    @SelectProvider(type = SqlProvider.class, method = "list")
    List<T> list(T t);

    /**
     * 查询总数，所有不为null的字段相等为条件
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
    List<T> listByIds(@Param("ids") Iterable<? extends Serializable> ids);
}
