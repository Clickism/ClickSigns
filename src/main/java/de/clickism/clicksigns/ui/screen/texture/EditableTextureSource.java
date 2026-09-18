package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.editable.Editable;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class EditableTextureSource {
    private final Map<UUID, Editable<TextureProcessor>> processorMap = new HashMap<>();
    private final List<Editable<TextureProcessor>> processors;
    private final List<Runnable> onChangeListeners = new ArrayList<>();
    private ResourceLocation base;

    public EditableTextureSource(TextureSource textureSource) {
        this.base = textureSource.base();
        this.processors = textureSource.processors().stream()
            .map(Editable::createRandom)
            .collect(Collectors.toCollection(ArrayList::new));
        this.processors.forEach(p ->
            this.processorMap.put(p.id(), p));
    }

    public EditableTextureSource onTextureSourceChanged(Runnable listener) {
        this.onChangeListeners.add(listener);
        return this;
    }

    private void notifyListeners() {
        for (Runnable listener : onChangeListeners) {
            listener.run();
        }
    }

    public ResourceLocation base() {
        return base;
    }

    public void base(ResourceLocation base) {
        this.base = base;
    }

    public List<Editable<TextureProcessor>> processors() {
        return Collections.unmodifiableList(processors);
    }

    public void addProcessor(TextureProcessor processor) {
        var editable = Editable.createRandom(processor);
        this.processors.add(editable);
        this.processorMap.put(editable.id(), editable);
        notifyListeners();
    }

    public void removeProcessor(Editable<TextureProcessor> processor) {
        this.processors.remove(processor);
        this.processorMap.remove(processor.id());
        notifyListeners();
    }

    public void updateProcessor(UUID id, UnaryOperator<TextureProcessor> updater) {
        var editable = this.processorMap.get(id);
        editable.update(updater);
        notifyListeners();
    }

    public void moveProcessor(UUID id, int indexDelta) {
        var editable = this.processorMap.get(id);
        int currentIndex = this.processors.indexOf(editable);
        int newIndex = currentIndex + indexDelta;
        if (newIndex < 0 || newIndex >= this.processors.size()) {
            return; // Out of bounds, do nothing
        }
        Collections.swap(this.processors, currentIndex, newIndex);
        notifyListeners();
    }

    public TextureSource build() {
        return new TextureSource(base, processors.stream()
            .map(Editable::current)
            .toList());
    }
}
