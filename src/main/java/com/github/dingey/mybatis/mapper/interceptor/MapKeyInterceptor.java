package com.github.dingey.mybatis.mapper.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.github.dingey.mybatis.mapper.exception.MapperException;
import com.github.dingey.mybatis.mapper.utils.ClassUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.persistence.MapKey;

/**
 * @author d
 */
@SuppressWarnings("unused")
@Intercepts(@Signature(method = "handleResultSets", type = ResultSetHandler.class, args = {Statement.class}))
public class MapKeyInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MetaObject metaStatementHandler = getRealTarget(invocation);
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("mappedStatement");
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        String currentMethodName = id.substring(id.lastIndexOf(".") + 1);
        Method currentMethod = findMethod(className, currentMethodName);

        if (currentMethod == null || currentMethod.getAnnotation(MapKey.class) == null) {
            return invocation.proceed();
        }
        Statement statement = (Statement) invocation.getArgs()[0];
        Map.Entry<Class<?>, Class<?>> kvTypePair = getKVTypeOfReturnMap(currentMethod);
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        Class<?> rawType = getRawType(currentMethod);
        if (rawType == List.class) {
            return result2MapList(statement, typeHandlerRegistry, kvTypePair);
        } else if (rawType == Set.class) {
            return result2MapSet(statement, typeHandlerRegistry, kvTypePair);
        } else {
            return result2Map(statement, typeHandlerRegistry, kvTypePair);
        }
    }

    /**
     * 找到与指定函数名匹配的Method。
     */
    private Method findMethod(String className, String targetMethodName) throws Throwable {
        return ClassUtils.getMethodWithParent(Class.forName(className), targetMethodName);
    }

    /**
     * 获取函数返回Map中key-value的类型
     *
     * @param mapResults mapResults
     * @return left为key的类型，right为value的类型
     */
    private Map.Entry<Class<?>, Class<?>> getKVTypeOfReturnMap(Method mapResults) {
        Type returnType = mapResults.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (!Map.class.equals(parameterizedType.getRawType())) {
                throw new RuntimeException("使用MapKey,返回类型必须是java.util.Map类型！method=" + mapResults);
            }
            if (parameterizedType.getActualTypeArguments().length < 2) {
                throw new MapperException("使用MapKey,返回参数泛型类型数量必须为2" + mapResults);
            }
            if (parameterizedType.getActualTypeArguments()[1] instanceof ParameterizedType) {
                return new HashMap.SimpleEntry<>((Class<?>) parameterizedType.getActualTypeArguments()[0], (Class<?>) ((ParameterizedType) parameterizedType.getActualTypeArguments()[1]).getActualTypeArguments()[0]);
            } else {
                return new HashMap.SimpleEntry<>((Class<?>) parameterizedType.getActualTypeArguments()[0], (Class<?>) parameterizedType.getActualTypeArguments()[1]);
            }
        }
        return new HashMap.SimpleEntry<>(null, null);
    }

    private Class<?> getRawType(Method mapResults) {
        Type returnType = mapResults.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type vType = parameterizedType.getActualTypeArguments()[1];
            if (vType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) vType).getRawType();
            }
            return (Class<?>) vType;
        }
        return null;
    }

    /**
     * 将查询结果映射成Map，其中第一个字段作为key，第二个字段作为value.
     *
     * @param statement           statement
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param kvTypePair          函数指定返回Map key-value的类型
     * @return 结果
     * @throws SQLException 异常
     */
    private Object result2Map(Statement statement, TypeHandlerRegistry typeHandlerRegistry, Map.Entry<Class<?>, Class<?>> kvTypePair) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        Map<Object, Object> map = new LinkedHashMap<>();
        while (resultSet.next()) {
            Object k = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object v = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            map.put(k, v);
        }
        return Collections.singletonList(map);
    }

    private Object result2MapList(Statement statement, TypeHandlerRegistry typeHandlerRegistry, Map.Entry<Class<?>, Class<?>> kvTypePair) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        Map<Object, List<Object>> map = new LinkedHashMap<>();
        while (resultSet.next()) {
            Object k = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object v = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            List<Object> objects = map.computeIfAbsent(k, k1 -> new ArrayList<>());
            objects.add(v);
        }
        return Collections.singletonList(map);
    }

    private Object result2MapSet(Statement statement, TypeHandlerRegistry typeHandlerRegistry, Map.Entry<Class<?>, Class<?>> kvTypePair) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        Map<Object, Set<Object>> map = new LinkedHashMap<>();
        while (resultSet.next()) {
            Object k = this.getObject(resultSet, 1, typeHandlerRegistry, kvTypePair.getKey());
            Object v = this.getObject(resultSet, 2, typeHandlerRegistry, kvTypePair.getValue());
            Set<Object> objects = map.computeIfAbsent(k, k1 -> new HashSet<>());
            objects.add(v);
        }
        return Collections.singletonList(map);
    }

    /**
     * 结果类型转换。
     * <p>
     * 使用注册在MyBatis的typeHander，方便类型转换。
     *
     * @param resultSet           结果集
     * @param columnIndex         字段下标，从1开始
     * @param typeHandlerRegistry MyBatis里typeHandler的注册器，方便转换成用户指定的结果类型
     * @param javaType            要转换的Java类型
     * @return 结果值
     * @throws SQLException SQL异常
     */
    private Object getObject(ResultSet resultSet, int columnIndex, TypeHandlerRegistry typeHandlerRegistry, Class<?> javaType) throws SQLException {
        final TypeHandler<?> typeHandler = typeHandlerRegistry.hasTypeHandler(javaType)
                ? typeHandlerRegistry.getTypeHandler(javaType) : typeHandlerRegistry.getUnknownTypeHandler();
        return typeHandler.getResult(resultSet, columnIndex);
    }

    /**
     * 分离最后一个代理的目标对象
     */
    private static MetaObject getRealTarget(Invocation invocation) {
        MetaObject metaStatementHandler = SystemMetaObject.forObject(invocation.getTarget());
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        return metaStatementHandler;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}