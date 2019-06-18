package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author d
 */
class MapperMethod {
	private static final Map<Class<?>, Class<?>> CACHED_ENTITY = new HashMap<>();

	private MapperMethod() {
	}

	static Class<?> entity(ProviderContext context) {
		Class<?> aClass = CACHED_ENTITY.get(context.getMapperType());
		if (aClass == null) {
			Type[] genericInterfaces = context.getMapperType().getGenericInterfaces();
			ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
			Type type = pt.getActualTypeArguments()[0];
			aClass = (Class<?>) type;
			CACHED_ENTITY.put(context.getMapperType(), aClass);
		}
		return aClass;
	}
}
