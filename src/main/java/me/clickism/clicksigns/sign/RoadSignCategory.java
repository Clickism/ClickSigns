package me.clickism.clicksigns.sign;

public enum RoadSignCategory {
    TEMPLATE,
    PART,
    CUSTOM;

    public static RoadSignCategory fromString(String string) {
        return switch (string) {
            case "TEMPLATE" -> TEMPLATE;
            case "PART" -> PART;
            default -> CUSTOM;
        };
    }
}
