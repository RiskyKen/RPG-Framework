package moe.plushie.rpgeconomy.core.common.network.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.shop.client.gui.GuiShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncShops implements IMessage, IMessageHandler<MessageServerSyncShops, IMessage> {

    private String[] shopIdentifiers;
    
    public MessageServerSyncShops() {
    }
    
    public MessageServerSyncShops(String[] shopIdentifiers) {
        this.shopIdentifiers = shopIdentifiers;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shopIdentifiers.length);
        for (int i = 0; i < shopIdentifiers.length; i++) {
            ByteBufUtils.writeUTF8String(buf, shopIdentifiers[i]);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int shopCount = buf.readInt();
        shopIdentifiers = new String[shopCount];
        for (int i = 0; i < shopIdentifiers.length; i++) {
            shopIdentifiers[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncShops message, MessageContext ctx) {
        sendShopsToGui(message.shopIdentifiers);
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private void sendShopsToGui(String[] shopIdentifiers) {
        Minecraft.getMinecraft().addScheduledTask(new ShopsIdentifiersToGui(shopIdentifiers));
    }

    @SideOnly(Side.CLIENT)
    public static class ShopsIdentifiersToGui implements Runnable {

        private final String[] shopIdentifiers;

        public ShopsIdentifiersToGui(String[] shopIdentifiers) {
            this.shopIdentifiers = shopIdentifiers;
        }

        @Override
        public void run() {
            GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
            if (guiScreen instanceof GuiShop) {
                ((GuiShop) guiScreen).gotShopIdentifiersFromServer(shopIdentifiers);
            }
        }
    }
}
