package com.github.dingey.mybatis.mapper.core;

import com.github.dingey.mybatis.mapper.annotation.DeleteMark;
import com.github.dingey.mybatis.mapper.lambda.AbstractSelect;
import com.github.dingey.mybatis.mapper.lambda.AbstractSql;
import com.github.dingey.mybatis.mapper.utils.ClassUtils;
import com.github.dingey.mybatis.mapper.utils.Const;
import com.github.dingey.mybatis.mapper.utils.ProviderContextUtils;
import org.apache.ibatis.annotations.Param;
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
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":insert", v -> SourceProviderFactory.getSourceBuilder(entity).insert());
    }

    public static String insertBatch(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":insertBatch", v -> SourceProviderFactory.getSourceBuilder(entity).insertBatch());
    }

    public static String insertSelective(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":insertSelective", v -> SourceProviderFactory.getSourceBuilder(entity).insertSelective());
    }

    public static String updateById(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":update", v -> SourceProviderFactory.getSourceBuilder(entity).updateById());
    }

    public static String updateByIdSelective(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":updateSelective", v -> SourceProviderFactory.getSourceBuilder(entity).updateByIdSelective());
    }

    public static String updates(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":updateSelective", v -> SourceProviderFactory.getSourceBuilder(entity).updates());
    }

    public static String getById(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":getById", v -> SourceProviderFactory.getSourceBuilder(entity).getById());
    }

    public static String listByIds(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":listByIds", v -> SourceProviderFactory.getSourceBuilder(entity).listByIds());
    }

    public static String list(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":list", v -> SourceProviderFactory.getSourceBuilder(entity).list());
    }

    public static String count(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":count", v -> SourceProviderFactory.getSourceBuilder(entity).count());
    }

    public static String listAll(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":listAll", v -> SourceProviderFactory.getSourceBuilder(entity).listAll());
    }

    public static String countAll(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":countAll", v -> SourceProviderFactory.getSourceBuilder(entity).countAll());
    }

    public static String deleteById(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteById", v -> SourceProviderFactory.getSourceBuilder(entity).deleteById());
    }

    public static String deleteMarkById(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteMarkById", v -> SourceProviderFactory.getSourceBuilder(entity).deleteMarkById());
    }

    public static String deleteByIds(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteByIds", v -> SourceProviderFactory.getSourceBuilder(entity).deleteByIds());
    }

    public static String deleteMarkByIds(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        return sqlMap.computeIfAbsent(entity.getName() + ":deleteMarkByIds", v -> SourceProviderFactory.getSourceBuilder(entity).deleteMarkByIds());
    }

    public static String deleteSmartById(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        Boolean aBoolean = deleteMarkMap.computeIfAbsent(entity, v -> ClassUtils.getByAnnotation(DeleteMark.class, entity).isPresent());
        if (aBoolean) {
            return deleteMarkById(context);
        } else {
            return deleteById(context);
        }
    }

    public static String deleteSmartByIds(ProviderContext context) {
        Class<?> entity = ProviderContextUtils.entity(context);
        Boolean aBoolean = deleteMarkMap.computeIfAbsent(entity, v -> ClassUtils.getByAnnotation(DeleteMark.class, entity).isPresent());
        if (aBoolean) {
            return deleteMarkByIds(context);
        } else {
            return deleteByIds(context);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static String lambda(ProviderContext context, @Param(Const.PARAM) AbstractSql statement) {
        bindEntityClass(statement, context);
        return statement.toSql();
    }

    @SuppressWarnings({"rawtypes"})
    public static String select(ProviderContext context, @Param(Const.PARAM) AbstractSelect select) {
        bindEntityClass(select, context);
        return select.toSql();
    }

    @SuppressWarnings({"rawtypes"})
    public static String selectCount(ProviderContext context, @Param(Const.PARAM) AbstractSelect select) {
        bindEntityClass(select, context);
        String s = select.toSql();
        return "<script>SELECT COUNT(0)" + s.substring(s.indexOf(" FROM "));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void bindEntityClass(AbstractSql statement, ProviderContext context) {
        if (statement.getEntityClass() == null) {
            statement.setEntityClass(ProviderContextUtils.entity(context));
        }
    }
}
