package moe.plushie.rpgeconomy.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.loot.common.LootTableHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        EntityPlayerMP player = ctx.getServerHandler().player;
        server.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                switch (message.syncType) {
                case CURRENCIES:
                    RpgEconomy.getProxy().getCurrencyManager().syncToClient(player);
                    break;
                case SHOPS_IDENTIFIERS:
                    RpgEconomy.getProxy().getShopManager().syncToClient(player);
                    break;
                case MAIL_SYSTEMS:
                    RpgEconomy.getProxy().getMailSystemManager().syncToClient(player);
                    break;
                case LOOT_TABLES:
                    LootTableHelper.syncLootTablesToClient(player);
                    break;
                case LOOT_POOLS:
                    LootTableHelper.syncLootPoolsToClient(player);
                    break;
                }
            }
        });
        return null;
    }

    public static enum SyncType {
        CURRENCIES,
        SHOPS_IDENTIFIERS,
        MAIL_SYSTEMS,
        LOOT_TABLES,
        LOOT_POOLS
    }
}
