package cc.xylitol.ui.gui.alt;

import java.util.concurrent.ThreadLocalRandom;

public final class StringUtils {
    public static final String ALPHA_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public static String replace(String s,Object... o) {
        for (int i = 0; i < o.length; i++) {
            s = s.replace(build("{",i,"}"),o[i].toString());
        }

        return s;
    }
    public static String breakString(String s) {
        StringBuilder sb = new StringBuilder();
        String[] sArray = s.split("");
        int index = 0;
        for (String s1 : sArray) {
            if (s1.equals("")) continue;

            if (s1.equals(s1.toUpperCase()) && Character.isLetter(s1.toCharArray()[0])) {
                if (index != 0) {
                    sb.append(" ");
                }
            }

            sb.append(s1);
            index++;
        }

        return sb.toString();
    }


    public static String build(Object... objects) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (Object o : objects) {
            stringBuilder.append(o);
        }

        return stringBuilder.toString();
    }

    public static String randomString(String pool, int length) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(pool.charAt(ThreadLocalRandom.current().nextInt(0,pool.length())));
        }

        return builder.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotNullOrEmpty(String s) {
        return s != null && isNotEmpty(s);
    }

    public static boolean isNotEmpty(String s) {
        return s.length() != 0;
    }
}
