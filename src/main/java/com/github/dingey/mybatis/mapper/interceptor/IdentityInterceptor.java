package com.github.dingey.mybatis.mapper.interceptor;

import com.github.dingey.mybatis.mapper.utils.ProviderContextUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("rawtypes")
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class IdentityInterceptor implements Interceptor {
    private static final Log log = LogFactory.getLog(IdentityInterceptor.class);
    private Field keyPropField;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!support(invocation)) {
            return invocation.proceed();
        }
        Object arg = invocation.getArgs()[1];
        Class<?> argClass = arg.getClass();
        if (isParamMap(arg)) {
            Object type = getTypeFromList((MapperMethod.ParamMap) arg);
            if (type != null) {
                argClass = type.getClass();
            }
        }
        Field id = ProviderContextUtils.id(argClass);
        if (id != null) {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            String[] keyProperties = ms.getKeyProperties();
            if (keyProperties == null || keyProperties.length < 1) {
                fullKeyProperties(id, ms);
            }
        }
        return invocation.proceed();
    }

    public static boolean support(Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        return SqlCommandType.INSERT.equals(ms.getSqlCommandType()) && (ms.getId().endsWith("insert") || ms.getId().endsWith("insertBatch"));
    }

    private boolean isParamMap(Object arg) {
        return arg instanceof MapperMethod.ParamMap;
    }

    private Object getTypeFromList(MapperMethod.ParamMap paramMap) {
        Object list = paramMap.get("list");
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            List l = (List) list;
            if (!l.isEmpty()) {
                return l.get(0);
            }
        }
        return null;
    }

    private void fullKeyProperties(Field id, MappedStatement ms) {
        try {
            if (keyPropField == null) {
                keyPropField = ms.getClass().getDeclaredField("keyProperties");
                if (!keyPropField.isAccessible()) {
                    keyPropField.setAccessible(true);
                }
            }
            String[] keyProps = new String[]{id.getName()};
            keyPropField.set(ms, keyProps);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
