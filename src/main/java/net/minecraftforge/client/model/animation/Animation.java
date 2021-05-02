package net.minecraftforge.client.model.animation;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public enum Animation implements IResourceManagerReloadListener {
    INSTANCE;

    private float clientPartialTickTime;

    /**
     * Get the global world time for the current tick, in seconds.
     */
    public static float getWorldTime(World world) {
        return getWorldTime(world, 0);
    }

    /**
     * Get the global world time for the current tick + partial tick progress, in seconds.
     */
    public static float getWorldTime(World world, float tickProgress) {
        return (world.getTotalWorldTime() + tickProgress) / 20;
    }

    /**
     * Get current partialTickTime.
     */
    public static float getPartialTickTime() {
        return INSTANCE.clientPartialTickTime;
    }

    /**
     * Load a new instance if AnimationStateMachine at specified location, with specified custom parameters.
     */
    public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters) {
        try {
            ClipResolver clipResolver = new ClipResolver();
            ParameterResolver parameterResolver = new ParameterResolver(customParameters);
            Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver(clipResolver);
            TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver(parameterResolver);
            IResource resource = manager.getResource(location);
            AnimationStateMachine asm = asmGson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), AnimationStateMachine.class);
            clipResolver.asm = asm;
            parameterResolver.asm = asm;
            asm.initialize();
            return asm;
        } catch (IOException | JsonParseException e) {
            FMLLog.log(Level.ERROR, e, "Exception loading Animation State Machine %s, skipping", location);
            return missing;
        } finally {
            Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver(null);
            TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver(null);
        }
    }

    /**
     * Load armature associated with a vanilla model.
     */
    public ModelBlockAnimation loadVanillaAnimation(ResourceLocation armatureLocation) {
        try {
            IResource resource;
            try {
                resource = manager.getResource(armatureLocation);
            } catch (FileNotFoundException e) {
                // this is normal. FIXME: error reporting?
                return defaultModelBlockAnimation;
            }
            return mbaGson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), ModelBlockAnimation.class);
        } catch (IOException | JsonParseException e) {
            FMLLog.log(Level.ERROR, e, "Exception loading vanilla model animation %s, skipping", armatureLocation);
            return defaultModelBlockAnimation;
        }
    }

    private IResourceManager manager;

    private final AnimationStateMachine missing = new AnimationStateMachine(
        ImmutableMap.of(),
        ImmutableMap.of("missingno", Clips.IdentityClip.instance),
        ImmutableList.of("missingno"),
        ImmutableMap.of(),
        "missingno");

    {
        missing.initialize();
    }

    private final Gson asmGson = new GsonBuilder()
        .registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE)
        .registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE)
        .registerTypeAdapterFactory(Clips.CommonClipTypeAdapterFactory.INSTANCE)
        //.registerTypeAdapterFactory(ClipProviders.CommonClipProviderTypeAdapterFactory.INSTANCE)
        .registerTypeAdapterFactory(TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE)
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .disableHtmlEscaping()
        .create();

    private static final class ClipResolver implements Function<String, IClip> {
        private AnimationStateMachine asm;

        public IClip apply(String name) {
            return asm.getClips().get(name);
        }
    }

    private static final class ParameterResolver implements Function<String, ITimeValue> {
        private final ImmutableMap<String, ITimeValue> customParameters;
        private AnimationStateMachine asm;

        public ParameterResolver(ImmutableMap<String, ITimeValue> customParameters) {
            this.customParameters = customParameters;
        }

        public ITimeValue apply(String name) {
            if (asm.getParameters().containsKey(name)) {
                return asm.getParameters().get(name);
            }
            return customParameters.get(name);
        }
    }

    private final Gson mbaGson = new GsonBuilder()
        .registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE)
        .registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE)
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .disableHtmlEscaping()
        .create();

    private final ModelBlockAnimation defaultModelBlockAnimation = new ModelBlockAnimation(ImmutableMap.of(), ImmutableMap.of());

    /**
     * Internal hook, do not use.
     */
    public static void setClientPartialTickTime(float clientPartialTickTime) {
        Animation.INSTANCE.clientPartialTickTime = clientPartialTickTime;
    }

    /**
     * Internal hook, do not use.
     */
    public void onResourceManagerReload(IResourceManager manager) {
        this.manager = manager;
    }
}
