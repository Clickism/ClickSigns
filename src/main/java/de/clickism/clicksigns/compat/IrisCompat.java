package de.clickism.clicksigns.compat;

import de.clickism.clicksigns.platform.Platform;

/**
 * Compatibility class for the Iris shader mod.
 */
public class IrisCompat {
    private static final boolean IRIS_LOADED = Platform.get().isModLoaded("iris");

    /**
     * Checks if the Iris shader mod is loaded and a shader pack is currently in use.
     *
     * @return true if Iris is loaded and a shader pack is in use, false otherwise
     */
    public static boolean isShaderPackInUse() {
        if (!IRIS_LOADED) {
            return false;
        }
        try {
            return net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
        } catch (Throwable e) {
            return false;
        }
    }
}
