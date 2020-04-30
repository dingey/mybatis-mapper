package com.github.dingey.mybatis.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 是否将查询结果映射成map：key列1，value列2
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface MapResults {
}
