package cc.xylitol.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextUtil {
    private static int[][] getGlyph(char character) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 16, 16);

        Font font = new Font("Courier", Font.PLAIN, 12);
        g.setFont(font);
        g.setColor(Color.BLACK);

        g.drawString(String.valueOf(character), 0, 12);

        int[][] res = new int[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                res[i][j] = (image.getRGB(j, i) == Color.WHITE.getRGB()) ? 0 : 1;
            }
        }

        g.dispose();

        return res;
    }

    private static String[] getCompressedGlyph(char character) {
        int[][] r = getGlyph(character);
        StringBuilder[] builder = new StringBuilder[4];

        for (int i = 0; i < 4; i++) {
            builder[i] = new StringBuilder();
        }

        String[] res = new String[4];
        for (int i = 0; i < 16; i += 4) {
            for (int j = 0; j < 16; j += 2) {
                int value = r[i][j] + r[i + 1][j] * 2 + r[i + 2][j] * 4 + r[i][j + 1] * 8 + r[i + 1][j + 1] * 16 + r[i + 2][j + 1] * 32 + r[i + 3][j] * 64 + r[i + 3][j + 1] * 128;
//                builder[i / 4].append((char) (2800));
                builder[i / 4].append((char) (10240 + value));
            }
        }
        for (int i = 0; i < 4; i++) {
            res[i] = builder[i].toString();
        }
        return res;
    }

    public static String[] getGlyph(String s) {
        s = s.trim();
        if (s.isEmpty()) return null;
        StringBuilder[] builder = new StringBuilder[4];

        for (int i = 0; i < 4; i++) {
            builder[i] = new StringBuilder();
        }

        String[] res = new String[4];
        for (int i = 0; i < s.length(); i++) {
            String[] ch = getCompressedGlyph(s.charAt(i));
            for (int j = 0; j < 4; j++) {
                builder[j].append(ch[j]);
            }
        }
        for (int i = 0; i < 4; i++) {
            res[i] = builder[i].toString();
        }
        return res;
    }

    public static String replace(final String string, final String searchChars, String replaceChars) {
        if(string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if(replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for(int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if(start == -1) {
                if(i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        return stringBuilder.toString();
    }
}