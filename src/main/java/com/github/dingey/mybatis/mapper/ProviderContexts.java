package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author d
 * @since 0.4.0
 */
public class ProviderContexts {
    private static final Map<Class<?>, Class<?>> entityCacheMap = new HashMap<>();
    private static final Map<Class<?>, Field> idMap = new HashMap<>();

    private ProviderContexts() {
    }

    /**
     * 获取主键field
     *
     * @param entity 模型对象
     * @return SQL
     */
    static Field id(Class<?> entity) {
        Field id = null;
        if (idMap.containsKey(entity)) {
            id = idMap.get(entity);
        } else {
            for (Field f : ClassUtil.getDeclaredFields(entity)) {
                if (f.isAnnotationPresent(Id.class)) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    id = f;
                    idMap.put(entity, f);
                    break;
                }
            }
        }
        return id;
    }

    static Class<?> entity(ProviderContext context) {
        Class<?> aClass = entityCacheMap.get(context.getMapperType());
        if (aClass == null) {
            Type[] genericInterfaces = context.getMapperType().getGenericInterfaces();
            ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
            Type type = pt.getActualTypeArguments()[0];
            aClass = (Class<?>) type;
            entityCacheMap.put(context.getMapperType(), aClass);
        }
        return aClass;
    }
}
