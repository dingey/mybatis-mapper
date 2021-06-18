package com.github.dingey.mybatis.mapper.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author d
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
