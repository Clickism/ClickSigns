package me.clickism.clicksigns.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;

import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AxisOrganizer {
    public enum Axis {
        HORIZONTAL, VERTICAL;
    }

    private final Axis axis;
    private final int defaultPadding;
    private final int startingX;
    private final int startingY;
    private final boolean centered;

    private final List<Widget> widgets = new LinkedList<>();

    private AxisOrganizer(Axis axis, int x, int y, boolean centered, int defaultPadding) {
        this.axis = axis;
        this.startingX = x;
        this.startingY = y;
        this.centered = centered;
        this.defaultPadding = defaultPadding;
    }

    public static AxisOrganizer horizontal(int x, int y, boolean centered, int defaultPadding) {
        return new AxisOrganizer(Axis.HORIZONTAL, x, y, centered, defaultPadding);
    }

    public static AxisOrganizer vertical(int x, int y, boolean centered, int defaultPadding) {
        return new AxisOrganizer(Axis.VERTICAL, x, y, centered, defaultPadding);
    }

    public AxisOrganizer organize(Widget widget) {
        return organize(defaultPadding, widget);
    }

    public AxisOrganizer organize(int padding, Widget widget) {
        if (centered) {
            center(widget);
        }
        if (widgets.isEmpty()) {
            setAxisPosition(widget, getStartingAxisPosition());
            widgets.add(widget);
            return this;
        }
        Widget last = widgets.get(widgets.size() - 1);
        int axisPos = getAxisPosition(last) + getLength(last) + padding;
        setAxisPosition(widget, axisPos);
        widgets.add(widget);
        return this;
    }

    public int getLastX() {
        Widget last = widgets.get(widgets.size() - 1);
        return last.getX() + last.getWidth();
    }

    public int getLastY() {
        Widget last = widgets.get(widgets.size() - 1);
        return last.getY() + last.getHeight();
    }

    private void center(Widget widget) {
        int centeredPos = getCenterAxisPosition() - getCenterOffset(widget);
        setSideAxisPosition(widget, centeredPos);
    }

    private int getLength(Widget widget) {
        return switch (this.axis) {
            case HORIZONTAL -> widget.getWidth();
            case VERTICAL -> widget.getHeight();
        };
    }

    private int getCenterOffset(Widget widget) {
        return switch (this.axis) {
            case HORIZONTAL -> widget.getHeight() / 2;
            case VERTICAL -> widget.getWidth() / 2;
        };
    }

    private int getCenterAxisPosition() {
        return switch (this.axis) {
            case HORIZONTAL -> startingY;
            case VERTICAL -> startingX;
        };
    }

    private int getStartingAxisPosition() {
        return switch (this.axis) {
            case HORIZONTAL -> startingX;
            case VERTICAL -> startingY;
        };
    }

    private int getAxisPosition(Widget widget) {
        return switch (this.axis) {
            case HORIZONTAL -> widget.getX();
            case VERTICAL -> widget.getY();
        };
    }

    private int getSideAxisPosition(Widget widget) {
        return switch (this.axis) {
            case HORIZONTAL -> widget.getY();
            case VERTICAL -> widget.getX();
        };
    }

    private void setAxisPosition(Widget widget, int position) {
        switch (this.axis) {
            case HORIZONTAL -> widget.setX(position);
            case VERTICAL -> widget.setY(position);
        }
    }

    private void setSideAxisPosition(Widget widget, int position) {
        switch (this.axis) {
            case HORIZONTAL -> widget.setY(position);
            case VERTICAL -> widget.setX(position);
        }
    }
}
