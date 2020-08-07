package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class IdentityInterceptor implements Interceptor {
    private static final Log log = LogFactory.getLog(IdentityInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object arg = invocation.getArgs()[1];
        Field id = SqlProvider.idNuable(arg.getClass());
        if (id != null && id.get(arg) == null) {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            String[] keyProperties = ms.getKeyProperties();
            if (keyProperties == null || keyProperties.length < 1) {
                fullKeyProperties(id, ms);
            }
        }
        return invocation.proceed();
    }

    private void fullKeyProperties(Field id, MappedStatement ms) {
        try {
            Field keyPropField = ms.getClass().getDeclaredField("keyProperties");
            if (!keyPropField.isAccessible()) {
                keyPropField.setAccessible(true);
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
