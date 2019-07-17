package moe.plushie.rpg_framework.core.common.network.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.loot.client.gui.GuiLootEditor;
import moe.plushie.rpg_framework.loot.common.LootTableHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncLoot implements IMessage {

    private ILootTable table;
    private ILootTablePool pool;

    public MessageServerSyncLoot() {
    }

    public MessageServerSyncLoot(ILootTablePool pool) {
        this.pool = pool;
    }

    public MessageServerSyncLoot(ILootTable table) {
        this.table = table;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        String jsonString = "";
        if (table != null) {
            jsonString = LootTableHelper.tableToJson(table).toString();
            buf.writeBoolean(true);
        } else if (pool != null) {
            jsonString = LootTableHelper.poolToJson(pool).toString();
            buf.writeBoolean(false);
        } else {
            RpgEconomy.getLogger().warn("Sending loot package with no data.");
        }
        ByteBufUtils.writeUTF8String(buf, jsonString);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean isTable = buf.readBoolean();
        String jsonString = ByteBufUtils.readUTF8String(buf);
        if (isTable) {
            table = LootTableHelper.tableFromJson(jsonString);
        } else {
            pool = LootTableHelper.poolFromJson(jsonString);
        }
    }

    public static class Handler implements IMessageHandler<MessageServerSyncLoot, IMessage> {

        @Override
        public IMessage onMessage(MessageServerSyncLoot message, MessageContext ctx) {
            sendToGui(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void sendToGui(MessageServerSyncLoot message) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (mc.currentScreen != null && mc.currentScreen instanceof GuiLootEditor) {
                        if (message.table != null) {
                            ((GuiLootEditor) mc.currentScreen).gotTableFromServer(message.table);
                        }
                        if (message.pool != null) {
                            ((GuiLootEditor) mc.currentScreen).gotPoolFromServer(message.pool);
                        }
                    }
                }
            });
        }
    }
}
