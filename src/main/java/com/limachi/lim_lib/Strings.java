package com.limachi.lim_lib;

import java.util.regex.Pattern;

public class Strings {
    public static Pattern CAMEL_TO_SNAKE_REGEX = Pattern.compile("\\B([A-Z])");
    public static String camel_to_snake(String str) {
        if (str == null || str.isEmpty()) return "";
        return CAMEL_TO_SNAKE_REGEX.matcher(str).replaceAll("_$1").toLowerCase();
    }

    public static String get_file(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        return tp.length > 0 ? tp[tp.length - 1] : path;
    }

    public static String get_folder(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        return tp.length > 1 ? tp[tp.length - 2] : "";
    }

    public static String get_folder_path(char separator, String path) {
        if (path == null || path.isEmpty()) return "";
        String[] tp = path.split("[" + separator + "]");
        if (tp.length <= 1) return ".";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < tp.length - 2; ++i)
            out.append(tp[i]).append(separator);
        return out.append(tp[tp.length - 2]).toString();
    }
}
