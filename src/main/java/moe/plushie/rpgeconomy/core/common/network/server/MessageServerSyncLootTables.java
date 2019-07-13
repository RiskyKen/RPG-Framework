package moe.plushie.rpgeconomy.core.common.network.server;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.client.gui.manager.GuiManager;
import moe.plushie.rpgeconomy.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.loot.client.gui.GuiLootEditor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncLootTables implements IMessage {

    private SyncType syncType;
    private ArrayList<IIdentifier> identifiers;
    private ArrayList<String> names;
    private ArrayList<String> categories;

    public MessageServerSyncLootTables() {
    }

    public MessageServerSyncLootTables(SyncType syncType, ArrayList<IIdentifier> identifiers, ArrayList<String> names, ArrayList<String> categories) {
        this.syncType = syncType;
        this.identifiers = identifiers;
        this.names = names;
        this.categories = categories;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(syncType.ordinal());
        buf.writeInt(identifiers.size());
        for (int i = 0; i < identifiers.size(); i++) {
            ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(identifiers.get(i)).toString());
            ByteBufUtils.writeUTF8String(buf, names.get(i));
            ByteBufUtils.writeUTF8String(buf, categories.get(i));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        syncType = SyncType.values()[buf.readInt()];
        int count = buf.readInt();
        identifiers = new ArrayList<IIdentifier>();
        names = new ArrayList<String>();
        categories = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            identifiers.add(IdentifierSerialize.deserializeJson(SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf))));
            names.add(ByteBufUtils.readUTF8String(buf));
            categories.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class Handler implements IMessageHandler<MessageServerSyncLootTables, IMessage> {

        @Override
        public IMessage onMessage(MessageServerSyncLootTables message, MessageContext ctx) {
            sendToGui(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void sendToGui(MessageServerSyncLootTables message) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    switch (message.syncType) {
                    case LOOT_TABLES:
                        if (mc.currentScreen != null && mc.currentScreen instanceof GuiLootEditor) {
                            ((GuiLootEditor) mc.currentScreen).tabLootTableEditor.onGotFromServer(message.identifiers, message.names, message.categories);
                        }
                        if (mc.currentScreen != null && mc.currentScreen instanceof GuiManager) {
                            ((GuiManager) mc.currentScreen).tabLootTableEditor.onGotFromServer(message.identifiers, message.names, message.categories);
                        }
                        break;
                    case LOOT_POOLS:
                        if (mc.currentScreen != null && mc.currentScreen instanceof GuiLootEditor) {
                            ((GuiLootEditor) mc.currentScreen).tabLootPoolEditor.onGotFromServer(message.identifiers, message.names, message.categories);
                        }
                        if (mc.currentScreen != null && mc.currentScreen instanceof GuiManager) {
                            ((GuiManager) mc.currentScreen).tabLootPoolEditor.onGotFromServer(message.identifiers, message.names, message.categories);
                        }
                        break;
                    }
                }
            });
        }
    }

    public static enum SyncType {
        LOOT_TABLES, LOOT_POOLS
    }
}
