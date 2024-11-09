package me.clickism.clicksigns.sign;

public enum Arrows {
    FORWARD,
    LEFT,
    RIGHT;

    public static Arrows fromString(String str) {
        return switch (str.toLowerCase()) {
            case "l" -> LEFT;
            case "r" -> RIGHT;
            case "f" -> FORWARD;
            default -> throw new IllegalArgumentException();
        };
    }
}
