package de.clickism.clicksigns.util;

public class ColorUtil {

    public static int argbToAbgr(int argb) {
        int a = ARGB.alpha(argb);
        int r = ARGB.red(argb);
        int g = ARGB.green(argb);
        int b = ARGB.blue(argb);
        return ABGR.abgr(a, b, g, r);
    }

    public static int abgrToArgb(int abgr) {
        int a = ABGR.alpha(abgr);
        int b = ABGR.blue(abgr);
        int g = ABGR.green(abgr);
        int r = ABGR.red(abgr);
        return ARGB.argb(a, r, g, b);
    }

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

    public static class ABGR {
        public static int alpha(int color) {
            return (color >> 24) & 0xFF;
        }

        public static int blue(int color) {
            return (color >> 16) & 0xFF;
        }

        public static int green(int color) {
            return (color >> 8) & 0xFF;
        }

        public static int red(int color) {
            return color & 0xFF;
        }

        public static int abgr(int a, int b, int g, int r) {
            return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((g & 0xFF) << 8) | (r & 0xFF);
        }
    }
}
