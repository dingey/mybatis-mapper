package com.github.dingey.mybatis.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 排序字段：{@code setOrderBy(" id desc,age asc")}
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface OrderBy {
}
