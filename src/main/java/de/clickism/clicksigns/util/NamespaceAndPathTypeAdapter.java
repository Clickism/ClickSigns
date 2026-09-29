package de.clickism.clicksigns.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import java.io.IOException;

public class NamespaceAndPathTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public ResourceLocation read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            in.nextNull();
            return null;
        } else {
            return ResourceLocation.tryParse(in.nextString());
        }
    }
}
