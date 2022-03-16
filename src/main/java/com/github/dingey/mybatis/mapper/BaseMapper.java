package com.github.dingey.mybatis.mapper;

import com.github.dingey.mybatis.mapper.annotation.DeleteMark;
import com.github.dingey.mybatis.mapper.core.SqlProvider;
import com.github.dingey.mybatis.mapper.lambda.AbstractInsert;
import com.github.dingey.mybatis.mapper.lambda.AbstractSelect;
import com.github.dingey.mybatis.mapper.lambda.Update;
import com.github.dingey.mybatis.mapper.utils.Const;
import org.apache.ibatis.annotations.*;

import javax.persistence.MapKey;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用模板
 *
 * @param <T> 数据模型
 * @author d
 */
@SuppressWarnings("unused")
public interface BaseMapper<T> {
    /**
     * 插入一条记录，忽略null的字段。
     * <p>生成的sql:
     * <p><pre>{@code insert into t
     * <trim prefix="(" suffix=")" suffixOverrides=",">
     *     <if test="col1 != null">
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
    int insert(T t);

    /**
     * 批量插入多条记录，包含null的字段。暂不支持oracle的序列的id批量设置和返回
     * <p>生成的sql:
     * <p><pre>{@code insert into t (col1,col2) values (#{col1}, #{col2}),(#{col1}, #{col2}) }
     * </pre>
     *
     * @param list 参数
     * @return 影响行数
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBatch")
    @Options(useGeneratedKeys = true)
    int insertBatch(@Param("list") List<T> list);

    /**
     * 根据主键更新一条记录，忽略null的字段
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
    int updateById(T t);

    /**
     * 根据update参数修改记录
     * <p>示例:
     * <p><pre>{@code new Update<User>().set(User::getName,"test").eq(User::getId,1L)}
     * 生成SQL为：UPDATE user SET name = ? WHERE id = ?
     * </pre>
     *
     * @param update 参数，支持lambda形式写法
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "lambda")
    int update(@Param(Const.PARAM) Update<T> update);

    /**
     * 根据delete构建删除语句
     * <p>示例:
     * <p><pre>{@code new Delete<User>().eq(User::getId,1L)}
     * 生成SQL为：DELETE FROM user WHERE id = 1L
     * </pre>
     *
     * @param delete 参数
     * @return 影响的行数
     */
    @UpdateProvider(type = SqlProvider.class, method = "lambda")
    int delete(@Param(Const.PARAM) com.github.dingey.mybatis.mapper.lambda.Delete<T> delete);

    /**
     * 根据select构建查询多条记录语句
     * <p>示例:
     * <p><pre>{@code new Select<User>().eq(User::getId,1L)}
     * 生成SQL为：SELECT * FROM user WHERE id = 1L
     * </pre>
     *
     * @param select 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "select")
    List<T> selectList(@Param(Const.PARAM) AbstractSelect<T, ?> select);

    /**
     * 根据select构建查询一条记录语句
     * <p>示例:
     * <p><pre>{@code new Select<User>().eq(User::getId,1L)}
     * 生成SQL为：SELECT * FROM user WHERE id = 1L
     * </pre>
     *
     * @param select 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "select")
    T selectOne(@Param(Const.PARAM) AbstractSelect<T, ?> select);

    /**
     * 根据select构建查询总数语句
     * <p>示例:
     * <p><pre>{@code new Select<User>().eq(User::getId,1L)}
     * 生成SQL为：SELECT COUNT(*) FROM user WHERE id = 1L
     * </pre>
     *
     * @param select 参数
     * @return 影响的行数
     */
    @SelectProvider(type = SqlProvider.class, method = "selectCount")
    long selectCount(@Param(Const.PARAM) AbstractSelect<T, ?> select);

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
    int deleteByIds(@Param(Const.IDS) Iterable<? extends Serializable> ids);

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
    List<T> listByIds(@Param(Const.IDS) Iterable<? extends Serializable> ids);

    /**
     * 根据select查询
     *
     * @param select select
     * @return 列表
     */
    @SelectProvider(type = SqlProvider.class, method = "select")
    List<Map<String, Object>> selectMaps(@Param(Const.PARAM) AbstractSelect<T, ?> select);

    /**
     * 根据 select 查询。注意： 只返回第一个字段的值
     *
     * @param select select
     * @return 返回第一个字段的值
     */
    @SelectProvider(type = SqlProvider.class, method = "select")
    List<Object> selectObjs(@Param(Const.PARAM) AbstractSelect<T, ?> select);

    /**
     * 根据 select 查询列1、列2并将结果返回为map。注意： 列1为key,列2为value
     *
     * @param select select
     * @return 影响的行数
     */
    @MapKey
    @SelectProvider(type = SqlProvider.class, method = "select")
    Map<Object, Object> selectKVMap(@Param(Const.PARAM) AbstractSelect<T, ?> select);

    /**
     * 执行插入,支持sql一些特性的插入操作
     *
     * @param insert insert
     * @return 影响的行数
     */
    @InsertProvider(type = SqlProvider.class, method = "lambda")
    int executeInsert(@Param(Const.PARAM) AbstractInsert<T, ?> insert);
}
