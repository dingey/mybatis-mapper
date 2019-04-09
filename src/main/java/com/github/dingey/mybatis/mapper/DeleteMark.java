package com.github.dingey.mybatis.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 删除标识字段：默认整形1删除；0正常；
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface DeleteMark {
}
