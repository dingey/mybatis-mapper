package com.github.dingey.mybatis.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author d
 * @since 0.4.0
 */
public class SourceProviderFactory {
    private static final Map<Class<?>, SourceScriptProvider<?>> map = new HashMap<>();

    public static <T> SourceScriptProvider<?> getSourceBuilder(Class<T> clazz) {
        return map.computeIfAbsent(clazz, v -> new SourceScriptProvider<>(clazz));
    }
}