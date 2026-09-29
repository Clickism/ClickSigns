package de.clickism.clicksigns.util;

public class ColorUtil {


    public static class ARGB {
        public static int alpha(int color) {
            return (color >> 24) & 0xFF;
        }

        public static int red(int color) {
            return (color >> 16) & 0xFF;
        }

        public static int green(int color) {
            return (color >> 8) & 0xFF;
        }

        public static int blue(int color) {
            return color & 0xFF;
        }

        public static int argb(int a, int r, int g, int b) {
            return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
        }
    }
}
