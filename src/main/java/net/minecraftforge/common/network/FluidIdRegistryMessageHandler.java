package net.minecraftforge.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class FluidIdRegistryMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.FluidIdMapMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForgeMessage.FluidIdMapMessage msg) throws Exception {
        FluidRegistry.initFluidIDs(msg.fluidIds, msg.defaultFluids);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FMLLog.log(Level.ERROR, cause, "FluidIdRegistryMessageHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
