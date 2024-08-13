package cc.xylitol.ui.font;


import cc.xylitol.Client;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    private static final String locate = "express/font/";
    public static RapeMasterFontManager font10;

    public static RapeMasterFontManager font12;
    public static RapeMasterFontManager font13;
    public static RapeMasterFontManager font14;
    public static RapeMasterFontManager font15;

    public static RapeMasterFontManager font16;
    public static RapeMasterFontManager font18;
    public static RapeMasterFontManager font19;
    public static RapeMasterFontManager font20;
    public static RapeMasterFontManager font22;
    public static RapeMasterFontManager font24;
    public static RapeMasterFontManager font26;

    public static RapeMasterFontManager font28;
    public static RapeMasterFontManager font32;
    public static RapeMasterFontManager font34;
    public static RapeMasterFontManager font40;
    public static RapeMasterFontManager font42;
    public static RapeMasterFontManager font64;

    public static RapeMasterFontManager other14;
    public static RapeMasterFontManager bold18;

    public static RapeMasterFontManager bold20;
    public static RapeMasterFontManager bold26;
    public static RapeMasterFontManager bold32;
    public static RapeMasterFontManager bold12;
    public static RapeMasterFontManager bold24;
    public static RapeMasterFontManager bold38;
    public static RapeMasterFontManager bold34;


    public static RapeMasterFontManager icontestFont40;
    public static RapeMasterFontManager icontestFont35;
    public static RapeMasterFontManager icontestFont75;

    public static RapeMasterFontManager icon22;
    public static RapeMasterFontManager museo18;

    public static RapeMasterFontManager tenacitybold;
    public static RapeMasterFontManager tenacitybold18;
    public static RapeMasterFontManager tenacitybold22;
    public static RapeMasterFontManager tenacitybold34;

    public static RapeMasterFontManager axBold20;
    public static RapeMasterFontManager axRegular18;

    public static RapeMasterFontManager material18;

    public static void init() {
        font10 = getFont("font.ttf", 10);


        font12 = getFont("font.ttf", 12);
        font13 = getFont("font.ttf", 13);
        font14 = getFont("font.ttf", 14);
        font15 = getFont("font.ttf", 15);

        font16 = getFont("font.ttf", 16);
        font18 = getFont("font.ttf", 18);
        font19 = getFont("font.ttf", 19);
        font20 = getFont("font.ttf", 20);
        font22 = getFont("font.ttf", 22);
        font24 = getFont("font.ttf", 24);
        font26 = getFont("font.ttf", 26);
        font28 = getFont("font.ttf", 28);
        font32 = getFont("font.ttf", 32);
        font34 = getFont("font.ttf", 34);

        font40 = getFont("font.ttf", 40);
        font42 = getFont("font.ttf", 42);
        font64 = getFont("font.ttf", 64);

        other14 = getFont("ico.ttf", 14);

        bold26 = getFont("bold.ttf", 26);
        bold32 = getFont("bold.ttf", 32);
        bold12 = getFont("bold.ttf", 12);
        bold18 = getFont("bold.ttf", 18);
        bold20 = getFont("bold.ttf", 20);
        bold24 = getFont("bold.ttf", 24);
        bold38 = getFont("bold.ttf", 38);
        bold34 = getFont("bold.ttf", 34);

        icontestFont40 = getFont("icont.ttf", 40);
        icontestFont35 = getFont("icont.ttf", 35);
        icontestFont75 = getFont("icont.ttf", 75);

        icon22 = getFont("iconfont.ttf", 22);
        museo18 = getFont("museo500.ttf", 18);

        tenacitybold = getFont("tenacity-bold.ttf", 20);
        tenacitybold22 = getFont("tenacity-bold.ttf", 22);
        tenacitybold34 = getFont("tenacity-bold.ttf", 34);
        tenacitybold18 = getFont("tenacity-bold.ttf", 18);

        axBold20 = getFont("ax-bold.ttf", 20);
        axRegular18 = getFont("ax-regular.ttf", 18);

        material18 = getFont("material.ttf", 18);
    }

    private static RapeMasterFontManager getFont(String fontName, float fontSize) {
        Font font = null;
        try {

            InputStream inputStream = Client.class.getResourceAsStream("/assets/minecraft/xylitol/font/" + fontName);
            assert inputStream != null;
            font = Font.createFont(Font.PLAIN, inputStream);
            font = font.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RapeMasterFontManager(font);
    }
}
