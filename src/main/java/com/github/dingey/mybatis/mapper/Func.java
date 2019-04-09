package com.github.dingey.mybatis.mapper;

@FunctionalInterface
interface Func<T> {
    String apply(T t);
}