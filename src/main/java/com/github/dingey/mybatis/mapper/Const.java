package com.github.dingey.mybatis.mapper;

/**
 * @author d
 */
final class Const {
    static final String SELECT_FROM = "select * from ";
    static final String AND = " and ";
    static final String WHERE = " where ";
    static final String AND1 = "} and ";
    static final String EL = "$";
    static boolean camelCase = true;
    static boolean columnUpper = false;
    static String tablePrefix = null;
    static boolean tableUpper = false;

    private Const() {
    }
}
