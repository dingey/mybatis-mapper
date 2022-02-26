package com.github.dingey.mybatis.mapper.lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参数绑定
 */
@SuppressWarnings("unused")
abstract class AbstractParameter {
    private Map<String, Object> params;
    private AtomicInteger paramCount;

    public AbstractParameter() {
        paramCount = new AtomicInteger(0);
        params = new HashMap<>();
    }

    public AbstractParameter(AtomicInteger paramCount, Map<String, Object> params) {
        this.paramCount = paramCount;
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    protected void addParam(String paramName, Object paramValue) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(paramName, paramValue);
    }

    protected Param createParam() {
        String name = "param" + paramCount.getAndIncrement();
        return new Param(name);
    }

    protected Param createParam(Object value) {
        String name = "param" + paramCount.getAndIncrement();
        addParam(name, value);
        return new Param(name);
    }

    AtomicInteger getParamCount() {
        return paramCount;
    }

    void setParamCount(AtomicInteger paramCount) {
        this.paramCount = paramCount;
    }

    void setParams(Map<String, Object> params) {
        this.params = params;
    }

    protected static class Param {
        private final String name;

        public Param(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String genExpression() {
            return "s.params." + name;
        }

        public String genELExpression() {
            return "#{s.params." + name + "}";
        }
    }
}
