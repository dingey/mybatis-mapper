package com.github.dingey.mybatis.mapper;

class StringUtil {
    private StringUtil() {
    }

    static String firstLower(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    static String firstUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static String camelCase(String s) {
        if (s.contains("_")) {
            String[] ss = s.split("_");
            StringBuilder sb = new StringBuilder(ss[0]);
            if (ss.length > 1) {
                for (int i = 1; i < ss.length; i++) {
                    sb.append(firstUpper(ss[i]));
                }
            }
            return sb.toString();
        } else {
            return s;
        }
    }

    static String upperCamelCase(String s) {
        return firstUpper(camelCase(s));
    }

    static String snakeCase(String camelCase) {
        if (camelCase != null && !camelCase.trim().isEmpty()) {
            char[] cs = camelCase.toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append(Character.toLowerCase(cs[0]));
            for (int i = 1; i < cs.length; i++) {
                if (Character.isUpperCase(cs[i])) {
                    sb.append("_").append(Character.toLowerCase(cs[i]));
                } else {
                    sb.append(camelCase.toCharArray()[i]);
                }
            }
            return sb.toString();
        } else {
            return camelCase;
        }
    }
}
