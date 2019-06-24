package moe.plushie.rpgeconomy.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientRequestSync implements IMessage, IMessageHandler<MessageClientRequestSync, IMessage> {

    private SyncType syncType;
    
    public MessageClientRequestSync() {
    }
    
    public MessageClientRequestSync(SyncType syncType) {
        this.syncType = syncType;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(syncType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        syncType = SyncType.values()[buf.readInt()];
    }

    @Override
    public IMessage onMessage(MessageClientRequestSync message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new SyncRequest(ctx.getServerHandler().player, message.syncType));
        return null;
    }
    
    public static enum SyncType {
        CURRENCIES,
        SHOPS_IDENTIFIERS,
        MAIL_SYSTEMS
    }
    
    public static class SyncRequest implements Runnable {
        
        private final EntityPlayerMP player;
        private final SyncType syncType;
        
        public SyncRequest(EntityPlayerMP player, SyncType syncType) {
            this.player = player;
            this.syncType = syncType;
        }

        @Override
        public void run() {
            switch (syncType) {
            case CURRENCIES:
                RpgEconomy.getProxy().getCurrencyManager().syncToClient(player);
                break;
            case SHOPS_IDENTIFIERS:
                RpgEconomy.getProxy().getShopManager().syncToClient(player);
                break;
            case MAIL_SYSTEMS:
                RpgEconomy.getProxy().getMailSystemManager().syncToClient(player);
                break;
            }
        }
    }
}
