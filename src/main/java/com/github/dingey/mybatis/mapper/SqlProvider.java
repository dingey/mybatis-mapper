package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author d
 * @since 0.4.0
 */
@SuppressWarnings("unused")
public class SqlProvider {
    private static final Map<String, String> sqlMap = new HashMap<>();
    private static final Map<Class<?>, Boolean> deleteMarkMap = new HashMap<>();

    public static String insert(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":insert", v -> SourceProviderFactory.getSourceBuilder(entity).insert());
    }

    public static String insertSelective(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":insertSelective", v -> SourceProviderFactory.getSourceBuilder(entity).insertSelective());
    }

    public static String updateById(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":update", v -> SourceProviderFactory.getSourceBuilder(entity).updateById());
    }

    public static String updateByIdSelective(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":updateSelective", v -> SourceProviderFactory.getSourceBuilder(entity).updateByIdSelective());
    }

    public static String updates(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":updateSelective", v -> SourceProviderFactory.getSourceBuilder(entity).updates());
    }

    public static String getById(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":getById", v -> SourceProviderFactory.getSourceBuilder(entity).getById());
    }

    public static String listByIds(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":listByIds", v -> SourceProviderFactory.getSourceBuilder(entity).listByIds());
    }

    public static String list(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":list", v -> SourceProviderFactory.getSourceBuilder(entity).list());
    }

    public static String count(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":count", v -> SourceProviderFactory.getSourceBuilder(entity).count());
    }

    public static String listAll(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":listAll", v -> SourceProviderFactory.getSourceBuilder(entity).listAll());
    }

    public static String countAll(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":countAll", v -> SourceProviderFactory.getSourceBuilder(entity).countAll());
    }

    public static String deleteById(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteById", v -> SourceProviderFactory.getSourceBuilder(entity).deleteById());
    }

    public static String deleteMarkById(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteMarkById", v -> SourceProviderFactory.getSourceBuilder(entity).deleteMarkById());
    }

    public static String deleteByIds(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteByIds", v -> SourceProviderFactory.getSourceBuilder(entity).deleteByIds());
    }

    public static String deleteMarkByIds(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteMarkByIds", v -> SourceProviderFactory.getSourceBuilder(entity).deleteMarkByIds());
    }

    public static String deleteSmartById(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        Boolean aBoolean = deleteMarkMap.computeIfAbsent(entity, v -> ClassUtil.getByAnnotation(DeleteMark.class, entity).isPresent());
        if (aBoolean) {
            return deleteMarkById(context);
        } else {
            return deleteById(context);
        }
    }

    public static String deleteSmartByIds(ProviderContext context) {
        Class<?> entity = ProviderContexts.entity(context);
        Boolean aBoolean = deleteMarkMap.computeIfAbsent(entity, v -> ClassUtil.getByAnnotation(DeleteMark.class, entity).isPresent());
        if (aBoolean) {
            return deleteMarkByIds(context);
        } else {
            return deleteByIds(context);
        }
    }
}
