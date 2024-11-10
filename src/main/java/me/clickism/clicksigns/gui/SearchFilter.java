package me.clickism.clicksigns.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SearchFilter<T> {
    private T filter;

    public SearchFilter() {
    }

    public SearchFilter(T filter) {
        this.filter = filter;
    }

    @Nullable
    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }
}
