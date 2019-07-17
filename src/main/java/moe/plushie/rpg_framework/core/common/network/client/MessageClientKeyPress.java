package moe.plushie.rpg_framework.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.lib.LibModKeys.ModKey;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientKeyPress implements IMessage, IMessageHandler<MessageClientKeyPress, IMessage> {

    private ModKey modKey;

    public MessageClientKeyPress() {
    }

    public MessageClientKeyPress(ModKey modKey) {
        this.modKey = modKey;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(modKey.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        modKey = ModKey.values()[buf.readInt()];
    }

    @Override
    public IMessage onMessage(MessageClientKeyPress message, MessageContext ctx) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                RpgEconomy.getProxy().onClentKeyPress(ctx.getServerHandler().player, message.modKey);
            }
        });
        return null;
    }
}
