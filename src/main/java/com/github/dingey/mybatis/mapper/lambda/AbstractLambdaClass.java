package com.github.dingey.mybatis.mapper.lambda;

import com.github.dingey.mybatis.mapper.utils.JpaUtils;
import com.github.dingey.mybatis.mapper.utils.LambdaUtils;

import java.lang.invoke.SerializedLambda;

/**
 * 绑定泛型关联的实体
 *
 * @param <T> 实体
 */
class AbstractLambdaClass<T> extends AbstractParameter {
    private Class<T> entityClass;

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected String table() {
        return JpaUtils.table(getEntityClass());
    }

    public String column(SFunction<T, ?> column) {
        SerializedLambda lambda = LambdaUtils.getSerializedLambda(column);
        if (entityClass == null) {
            entityClass = (Class<T>) LambdaUtils.getLambdaClass(lambda);
        }
        return JpaUtils.column(lambda);
    }
}
