package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class IRenderHandler {
    @SideOnly(Side.CLIENT)
    public abstract void render(float partialTicks, WorldClient world, Minecraft mc);
}
