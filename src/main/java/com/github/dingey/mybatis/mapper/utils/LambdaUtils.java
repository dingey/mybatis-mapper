package com.github.dingey.mybatis.mapper.utils;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author d
 */
public final class LambdaUtils {
    private static final Log log = LogFactory.getLog(LambdaUtils.class);
    private static final Map<Class<?>, SerializedLambda> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> IMPL_CLASS = new ConcurrentHashMap<>();

    public static Class<?> getLambdaClass(SerializedLambda lambda) {
        Class<?> aClass = IMPL_CLASS.get(lambda.getImplClass());
        if (aClass == null) {
            String s = lambda.getImplClass().replaceAll("/", ".");
            try {
                aClass = Class.forName(s);
                IMPL_CLASS.put(lambda.getImplClass(), aClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        return aClass;
    }

    public static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass());
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }
}
