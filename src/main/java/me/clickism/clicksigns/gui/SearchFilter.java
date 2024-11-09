package me.clickism.clicksigns.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SearchFilter<T> {
    private T filter;

    public SearchFilter() {
    }

    public SearchFilter(T filter) {
        this.filter = filter;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }
}
