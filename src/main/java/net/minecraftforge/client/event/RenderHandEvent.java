package net.minecraftforge.client.event;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderHandEvent extends Event {
    public final RenderGlobal context;
    public final float partialTicks;
    public final int renderPass;

    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass) {
        this.context = context;
        this.partialTicks = partialTicks;
        this.renderPass = renderPass;
    }
}
