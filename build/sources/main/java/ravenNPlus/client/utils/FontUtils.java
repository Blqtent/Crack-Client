package ravenNPlus.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import ravenNPlus.client.utils.animations.AnimationUtil;

import java.awt.*;
import java.util.Arrays;

public class FontUtils extends net.minecraft.client.gui.Gui {

    public static FontUtils instance;

    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static FontRenderer fr = mc.fontRendererObj;

    public static String drawStringWithShadowWithUnderline(String text, int x, int y, int color) {
        fr.drawStringWithShadow(text, x, y, color);
        drawRect(x-2, y+fr.FONT_HEIGHT+1, x+fr.getStringWidth(text)+2, y+fr.FONT_HEIGHT, color);
        return text;
    }

    public static String drawStringWithShadowWithUnderline(String text, int x, int y, int expand, int color) {
        fr.drawStringWithShadow(text, x, y, color);
        drawRect(x-expand, y+fr.FONT_HEIGHT+1, x+fr.getStringWidth(text)+expand, y+fr.FONT_HEIGHT, color);
        return text;
    }

    public static String drawStringWithUnderline(String text, int x, int y, int color) {
        fr.drawString(text, x, y, color);
        drawRect(x-2, y+fr.FONT_HEIGHT+1, x+fr.getStringWidth(text)+2, y+fr.FONT_HEIGHT, color);
        return text;
    }

    public static String drawStringWithUnderline(String text, int x, int y, int expand, int color) {
        fr.drawString(text, x, y, color);
        drawRect(x-expand, y+fr.FONT_HEIGHT+1, x+fr.getStringWidth(text)+expand, y+fr.FONT_HEIGHT, color);
        return text;
    }

    public static String drawToLowerCaseString(String text, int x, int y, int color) {
        fr.drawString(text.toLowerCase(), x, y, color);
        return text;
    }

    public static String drawToUpperCaseString(String text, int x, int y, int color) {
        fr.drawString(text.toUpperCase(), x, y, color);
        return text;
    }

    public static String drawStringWithTextSplit(String text, int x, int y, int split, int color) {
        fr.drawString(Arrays.toString(text.split(text, split)), x, y, color);
        return text;
    }

    public static int interpolateColor(int rgba1, int rgba2, float percent) {
        int r1 = rgba1 & 0xFF, g1 = rgba1 >> 8 & 0xFF, b1 = rgba1 >> 16 & 0xFF, a1 = rgba1 >> 24 & 0xFF;
        int r2 = rgba2 & 0xFF, g2 = rgba2 >> 8 & 0xFF, b2 = rgba2 >> 16 & 0xFF, a2 = rgba2 >> 24 & 0xFF;

        int r = (int) (r1 < r2 ? r1 + (r2 - r1) * percent : r2 + (r1 - r2) * percent);
        int g = (int) (g1 < g2 ? g1 + (g2 - g1) * percent : g2 + (g1 - g2) * percent);
        int b = (int) (b1 < b2 ? b1 + (b2 - b1) * percent : b2 + (b1 - b2) * percent);
        int a = (int) (a1 < a2 ? a1 + (a2 - a1) * percent : a2 + (a1 - a2) * percent);

        return r | g << 8 | b << 16 | a << 24;
    }

    public static void drawAnimatedString(String text, int x, int y) {
        fr.drawString(text, (int) (x+AnimationUtil.calculateCompensation(x+0F, x+40, 2, 6)), y, -1);
    }

    public static int drawLowerCountColor(int between) {
        int x = mc.thePlayer.getHeldItem().stackSize;

        if(x < 1+between) {
            return new Color(238, 0, 0).getRGB();
        } else if (x < 2+between) {
            return new Color(215, 25, 0).getRGB();
        } else if (x < 3+between) {
            return new Color(203, 37, 0).getRGB();
        } else if (x < 4+between) {
            return new Color(192, 49, 0).getRGB();
        } else if (x < 5+between) {
            return new Color(181, 61, 0).getRGB();
        } else if (x < 6+between) {
            return new Color(170, 74, 0).getRGB();
        } else if (x < 7+between) {
            return new Color(158, 86, 0).getRGB();
        } else if (x < 8+between) {
            return new Color(147, 98, 0).getRGB();
        } else if (x < 9+between) {
            return new Color(136, 110, 0).getRGB();
        } else if (x < 10+between) {
            return new Color(124, 122, 0).getRGB();
        } else if (x < 11+between) {
            return new Color(113, 134, 0).getRGB();
        } else if (x < 12+between) {
            return new Color(102, 146, 0).getRGB();
        } else if (x < 13+between) {
            return new Color(90, 158, 0).getRGB();
        } else if (x < 14+between) {
            return new Color(79, 170, 0).getRGB();
        } else if (x < 15+between) {
            return new Color(68, 182, 0).getRGB();
        } else if (x < 16+between) {
            return new Color(56, 194, 0).getRGB();
        } else if (x < 17+between) {
            return new Color(24, 219, 0).getRGB();
        } else if (x < 18+between) {
            return new Color(23, 231, 0).getRGB();
        } else if (x < 19+between) {
            return new Color(11, 243, 0).getRGB();
        } else if (x < 20+between) {
            return new Color(0, 255, 0).getRGB();
        } else return 0;
    }

    public static int pink = 0x0;

}