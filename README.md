## mybatis CRUD通用方法支持

## 用法
继承BaseMapper即可使用get/list/insert/update等方法
```
public interface SomeMapper extends BaseMapper<Some> {
}
// 支持jpa生成的实体对象，只需配置@Id注解即可调用主键查询方法
class Some{
  @Id
  Long id;
  String name;
}
```
## 方法一览
```
 /**
     * 插入一条记录，忽略null的字段。
     * <p>生成的sql:
     * <p><pre>insert into t
     * <trim prefix="(" suffix=")" suffixOverrides=",">
     *     <if test="col1 != null">
     *       col1,
     *     </if>
     * </trim>
     * <trim prefix="values (" suffix=")" suffixOverrides=",">
     *     <if test="col1 != null">
     *       #{col1},
     *     </if>
     * </trim>
     * </pre>
     *
     * @param t 参数
     * @return 影响的行数
     */
    int insert(T t);

    /**
     * 批量插入多条记录，包含null的字段。暂不支持oracle的序列的id批量设置和返回
     * <p>生成的sql:
     * <p><pre>insert into t (col1,col2) values (#{col1}, #{col2}),(#{col1}, #{col2})
     * </pre>
     * @param list 参数
     * @return 影响行数
     */
    int insertBatch(List<T> list);

    /**
     * 根据主键更新一条记录，忽略null的字段
     * <p>生成的sql:
     * <p><pre>update t
     * <set>
     *     <if test="col1 != null">
     *       col1 = #{col1},
     *     </if>
     * </set>
     * where id = #{id}
     * </pre>
     *
     * @param t 参数
     * @return 影响的行数
     */
    int updateById(T t);

    /**
     * 根据update参数修改记录
     * <p>示例:
     * <p><pre>new Update<User>().set(User::getName,"test").eq(User::getId,1L)
     * 生成SQL为：UPDATE user SET name = ? WHERE id = ?
     * </pre>
     *
     * @param update 参数，支持lambda形式写法
     * @return 影响的行数
     */
    int update(Update<T> update);

    /**
     * 根据delete构建删除语句
     * <p>示例:
     * <p><pre>new Delete<User>().eq(User::getId,1L)
     * 生成SQL为：DELETE FROM user WHERE id = 1L
     * </pre>
     *
     * @param delete 参数
     * @return 影响的行数
     */
    int delete(Delete<T> delete);

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
    List<T> selectList(Select<T> select);

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
    T selectOne(Select<T> select);

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
    long selectCount(Select<T> select);

    /**
     * 根据主键删除一条记录，如果有{@code DeleteMark}则假删除，否则真删除
     *
     * @param id 主键
     * @return 影响的行数
     * @see DeleteMark
     */
    int deleteById(Serializable id);

    /**
     * 根据主键删除一条记录，如果有DeleteMark则假删除，否则真删除。如果需要更新修改时间等，推荐用 {@link BaseMapper#update}方法
     *
     * @param ids 主键集合
     * @return 影响的行数
     * @see DeleteMark
     */
    int deleteByIds(Iterable<? extends Serializable> ids);

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 一条记录
     */
    T getById(Serializable id);

    /**
     * 查询一条记录，结果多于一条会报错
     *
     * @param t 参数
     * @return 影响的行数
     */
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
    List<T> list(T t);

    /**
     * 查询总数，所有不为null的字段相等为条件
     *
     * @param t 参数
     * @return 总数
     */
    long count(T t);

    /**
     * 查询所有
     *
     * @return 列表
     */
    List<T> listAll();

    /**
     * 汇总所有
     *
     * @return 总数
     */
    long countAll();

    /**
     * 根据主键批量查询
     *
     * @param ids 主键
     * @return 列表
     */
    List<T> listByIds(Iterable<? extends Serializable> ids);

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
    int update(Update<T> update);

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
    int delete(Delete<T> delete);

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
    List<T> selectList(AbstractSelect<T, ?> select);

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
    T selectOne(AbstractSelect<T, ?> select);

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
    long selectCount(AbstractSelect<T, ?> select);
```
## 扩展增强
支持将两列的结果隐射为Map格式,第1列为key,第2列为value。

```
@MapKey
Map<Long, Long> selectUserRoleId(Collection<Long> ids);

@MapKey
Map<Long, List<Long>> selectUserRoleIds(Collection<Long> ids);

@MapKey
Map<Long, Set<Long>> selectUserRoleIdsSet(Collection<Long> ids);
```
## lambda支持
提供Select、MysqlSelect、OracleSelect、Insert、Update形式的支持，满足单表的绝大多数操作场景；MysqlSelect支持mysql的ignore、replace、on duplicate key update、limt等操作;Update支持自增操作。
```
new Select<Man>()
.select(Man::getId, Man::getName)
.from(Man.class)
.eq(Man::getIsDel, 0)
.orderBy(Man::getId);

new Update<Man>().set(Man::getAge, 1).eq(Man::getId, 1);

new Insert<Man>()
.ignore() //生成 insert ignore into table
.replace() //生成 replace into table
.values(Arrays.asList(new Some()));// 批量插入

new Insert<Man>().insert(Some::getId,Some::getName).values(1,"a");//单个插入

```

## 原理
轻量级基于mybatis注解@InsertProvider/@UpdateProvider/@SelectProvider实现的。

## 依赖

```
<!-- 该依赖需要手动装配 -->
<dependency>
    <groupId>com.github.dingey</groupId>
    <artifactId>mybatis-mapper</artifactId>
    <version>0.5.0</version>
</dependency>

<!-- spring自动装配 -->
<dependency>
    <groupId>com.github.dingey</groupId>
    <artifactId>mybatis-mapper-spring-starter</artifactId>
    <version>0.5.1</version>
</dependency>
```
## 配置项
```
mybatis:
  mapper:
    column-upper: true  #列名大写，默认false
    camel-case: true    #开启驼峰转下划线，默认true
    table-prefix: T_    #表前缀，默认空
    table-upper: true   #表名大写，默认false
    map-key: true       #是否开启两列查询映射为Map<object,object>的支持，默认false
    strategy: identity/sequence  #开启全局的主键支持，默认identity
```

## 支持jpa部分注解
|  注解   | 是否支持  |
|  ----  | ----  |
| @Table  | 是 |
| @Column  | 是 |
| @SequenceGenerator  | 是 |
| @MapKey  | 是 |
