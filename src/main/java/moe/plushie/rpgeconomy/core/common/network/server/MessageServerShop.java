package moe.plushie.rpgeconomy.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.shop.client.gui.GuiShop;
import moe.plushie.rpgeconomy.shop.common.serialize.ShopSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerShop implements IMessage, IMessageHandler<MessageServerShop, IMessage> {

    private IShop shop = null;
    private boolean update = false;
    
    public MessageServerShop() {
    }

    public MessageServerShop(IShop shop, boolean update) {
        this.shop = shop;
        this.update = update;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(update);
        buf.writeBoolean(shop != null);
        if (shop != null) {
            ByteBufUtils.writeUTF8String(buf, shop.getIdentifier());
            JsonElement jsonShop = ShopSerializer.serializeJson(shop);
            ByteBufUtils.writeUTF8String(buf, jsonShop.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        update = buf.readBoolean();
        if (buf.readBoolean()) {
            String identifier = ByteBufUtils.readUTF8String(buf);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement jsonShop = SerializeHelper.stringToJson(jsonString);
            shop = ShopSerializer.deserializeJson(jsonShop, identifier);
        }
    }

    @Override
    public IMessage onMessage(MessageServerShop message, MessageContext ctx) {
        sendShopToGui(message.shop, message.update);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void sendShopToGui(IShop shop, boolean update) {
        Minecraft.getMinecraft().addScheduledTask(new ShopToGui(shop, update));
    }

    @SideOnly(Side.CLIENT)
    public static class ShopToGui implements Runnable {

        private final IShop shop;
        private final boolean update;
        
        public ShopToGui(IShop shop, boolean update) {
            this.shop = shop;
            this.update = update;
        }

        @Override
        public void run() {
            GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
            //RpgEconomy.getLogger().info(guiScreen);
            if (guiScreen instanceof GuiShop) {
                ((GuiShop) guiScreen).gotShopFromServer(shop, update);
            }
        }
    }
}
