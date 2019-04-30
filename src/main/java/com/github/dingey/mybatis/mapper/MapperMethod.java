package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author d
 */
class MapperMethod {
	private static final Map<Method, Class<?>> cachedEntity = new HashMap<>();

	private MapperMethod() {
	}

	static Class<?> entity(ProviderContext context) {
		Class<?> aClass = cachedEntity.get(context.getMapperMethod());
		if (aClass == null) {
			Type[] genericInterfaces = context.getMapperType().getGenericInterfaces();
			ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
			Type type = pt.getActualTypeArguments()[0];
			aClass = (Class<?>) type;
			cachedEntity.put(context.getMapperMethod(), aClass);
		}
		return aClass;
	}
}
