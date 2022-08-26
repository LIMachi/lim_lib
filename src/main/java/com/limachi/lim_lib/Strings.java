package com.limachi.lim_lib;

import java.util.regex.Pattern;

public class Strings {
    public static Pattern CAMEL_TO_SNAKE_REGEX = Pattern.compile("\\B([A-Z])");
    public static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) return "";
        return CAMEL_TO_SNAKE_REGEX.matcher(str).replaceAll("_$1").toLowerCase();
    }

    public static String getFile(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        return tp.length > 0 ? tp[tp.length - 1] : path;
    }

    public static String getFolder(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        return tp.length > 1 ? tp[tp.length - 2] : "";
    }

    public static String getSimplifiedClassName(String fullyQualifiedClassName) {
        String out = fullyQualifiedClassName;
        String[] cut = fullyQualifiedClassName.split("[.]");
        if (cut.length > 1)
            out = cut[cut.length - 1];
        cut = out.split("[$]");
        if (cut.length > 1)
            out = cut[cut.length - 1];
        cut = out.split("[(]");
        if (cut.length > 1)
            out = cut[0];
        return out;
    }

    public static String getFolderPath(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        if (tp.length <= 1) return ".";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < tp.length - 2; ++i)
            out.append(tp[i]).append(separator);
        return out.append(tp[tp.length - 2]).toString();
    }
}
