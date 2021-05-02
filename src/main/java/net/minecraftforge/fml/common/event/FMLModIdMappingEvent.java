package net.minecraftforge.fml.common.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Called whenever the ID mapping might have changed. If you register for this event, you
 * will be called back whenever the client or server loads an ID set. This includes both
 * when the ID maps are loaded from disk, as well as when the ID maps revert to the initial
 * state.
 * <p>
 * Note: you cannot change the IDs that have been allocated, but you might want to use
 * this event to update caches or other in-mod artifacts that might be impacted by an ID
 * change.
 *
 * @author cpw
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 */
public class FMLModIdMappingEvent extends FMLEvent {
    public enum RemapTarget {BLOCK, ITEM}

    public static class ModRemapping {
        public final int oldId;
        public final int newId;
        public final String tag;
        public final RemapTarget remapTarget;
        public final ResourceLocation resourceLocation;

        public ModRemapping(int oldId, int newId, ResourceLocation tag, RemapTarget type) {
            this.oldId = oldId;
            this.newId = newId;
            this.tag = tag.toString();
            this.remapTarget = type;
            this.resourceLocation = tag;
        }

    }

    public final ImmutableList<ModRemapping> remappedIds;
    public final boolean isFrozen;

    public FMLModIdMappingEvent(Map<ResourceLocation, Integer[]> blocks, Map<ResourceLocation, Integer[]> items, boolean isFrozen) {
        this.isFrozen = isFrozen;
        List<ModRemapping> remappings = Lists.newArrayList();
        for (Entry<ResourceLocation, Integer[]> mapping : blocks.entrySet()) {
            remappings.add(new ModRemapping(mapping.getValue()[0], mapping.getValue()[1], mapping.getKey(), RemapTarget.BLOCK));
        }
        for (Entry<ResourceLocation, Integer[]> mapping : items.entrySet()) {
            remappings.add(new ModRemapping(mapping.getValue()[0], mapping.getValue()[1], mapping.getKey(), RemapTarget.ITEM));
        }

        remappings.sort(Comparator.comparingInt(o -> o.newId));
        remappedIds = ImmutableList.copyOf(remappings);
    }
}
