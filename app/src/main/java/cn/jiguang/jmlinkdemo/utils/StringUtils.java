package cn.jiguang.jmlinkdemo.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class StringUtils {
    public static String getRandomName(int length) {
        if (length < 1) {
            return "";
        }
        char[] values = new char[length];
        for (int i = 0; i < length; i++) {
            values[i] = getRandomChar();
        }
        return new String(values);
    }

    private static char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }
}
