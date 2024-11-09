package me.clickism.clicksigns.sign;

public enum RoadSignTemplateCategory {
    TEMPLATE,
    PART,
    CUSTOM;

    public static RoadSignTemplateCategory fromString(String string) {
        return switch (string) {
            case "TEMPLATE" -> TEMPLATE;
            case "PART" -> PART;
            default -> CUSTOM;
        };
    }
}
