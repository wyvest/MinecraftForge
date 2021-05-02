package net.minecraftforge.client.model;

import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public interface ICustomModelLoader extends IResourceManagerReloadListener {
    /*
     * Checks if given model should be loaded by this loader.
     * Reading file contents is inadvisable, if possible decision should be made based on the location alone.
     */
    boolean accepts(ResourceLocation modelLocation);

    /*
     * loads (or reloads) specified model
     */
    IModel loadModel(ResourceLocation modelLocation) throws IOException;
}
