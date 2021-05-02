package net.minecraftforge.client.model.animation;

import com.google.common.base.Optional;
import net.minecraftforge.client.model.IModel;

/**
 * IModel that has animation data.
 */
public interface IAnimatedModel extends IModel {
    Optional<? extends IClip> getClip(String name);
}
