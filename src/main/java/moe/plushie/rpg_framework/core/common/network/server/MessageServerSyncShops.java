package moe.plushie.rpg_framework.core.common.network.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.shop.client.gui.GuiShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncShops implements IMessage, IMessageHandler<MessageServerSyncShops, IMessage> {

    private IIdentifier[] shopIdentifiers;
    private String[] shopNames;

    public MessageServerSyncShops() {
    }

    public MessageServerSyncShops(IIdentifier[] shopIdentifiers, String[] shopNames) {
        this.shopIdentifiers = shopIdentifiers;
        this.shopNames = shopNames;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shopIdentifiers.length);
        for (int i = 0; i < shopIdentifiers.length; i++) {
            ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(shopIdentifiers[i]).toString());
            ByteBufUtils.writeUTF8String(buf, shopNames[i]);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int shopCount = buf.readInt();
        shopIdentifiers = new IIdentifier[shopCount];
        shopNames = new String[shopCount];
        for (int i = 0; i < shopIdentifiers.length; i++) {
            shopIdentifiers[i] = IdentifierSerialize.deserializeJson(SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf)));
            shopNames[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncShops message, MessageContext ctx) {
        sendShopsToGui(message.shopIdentifiers, message.shopNames);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void sendShopsToGui(IIdentifier[] shopIdentifiers, String[] shopNames) {
        Minecraft.getMinecraft().addScheduledTask(new ShopsIdentifiersToGui(shopIdentifiers, shopNames));
    }

    @SideOnly(Side.CLIENT)
    public static class ShopsIdentifiersToGui implements Runnable {

        private final IIdentifier[] shopIdentifiers;
        private final String[] shopNames;

        public ShopsIdentifiersToGui(IIdentifier[] shopIdentifiers, String[] shopNames) {
            this.shopIdentifiers = shopIdentifiers;
            this.shopNames = shopNames;
        }

        @Override
        public void run() {
            GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
            if (guiScreen instanceof GuiShop) {
                ((GuiShop) guiScreen).gotShopIdentifiersFromServer(shopIdentifiers, shopNames);
            }
        }
    }
}
