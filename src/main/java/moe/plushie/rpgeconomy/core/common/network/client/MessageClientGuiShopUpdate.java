package moe.plushie.rpgeconomy.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiShopUpdate implements IMessage, IMessageHandler<MessageClientGuiShopUpdate, IMessage> {

    private ShopMessageType type;
    private String shopIdentifier;
    private IShop shop;
    private int tabIndex;
    
    public MessageClientGuiShopUpdate() {
    }
    
    public MessageClientGuiShopUpdate(ShopMessageType type) {
        this.type = type;
    }
    
    public MessageClientGuiShopUpdate setShopIdentifier(String shopIdentifier) {
        this.shopIdentifier = shopIdentifier;
        return this;
    }
    
    public MessageClientGuiShopUpdate setShop(IShop shop) {
        this.shop = shop;
        return this;
    }
    
    public MessageClientGuiShopUpdate setTabIndex(int value) {
        this.tabIndex = value;
        return this;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        switch (type) {
        case EDIT_MODE_ON:
            break;
        case EDIT_MODE_OFF:
            break;
        case SHOP_CHANGE:
            ByteBufUtils.writeUTF8String(buf, shopIdentifier);
        case TAB_CHANGED:
            buf.writeInt(tabIndex);
            break;
        default:
            break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = ShopMessageType.values()[buf.readInt()];
        switch (type) {
        case EDIT_MODE_ON:
            break;
        case EDIT_MODE_OFF:
            break;
        case SHOP_CHANGE:
            shopIdentifier = ByteBufUtils.readUTF8String(buf);
            break;
        case TAB_CHANGED:
            tabIndex = buf.readInt();
            break;
        default:
            break;
        }
    }

    @Override
    public IMessage onMessage(MessageClientGuiShopUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player.openContainer != null && player.openContainer instanceof ContainerShop) {
            ContainerShop containerShop = (ContainerShop) player.openContainer;
            switch (message.type) {
            case EDIT_MODE_ON:
                containerShop.setEditMode(true);
                break;
            case EDIT_MODE_OFF:
                containerShop.setEditMode(false);
                break;
            case SHOP_CHANGE:
                containerShop.setShopIdentifier(message.shopIdentifier);
                break;
            case TAB_CHANGED:
                containerShop.changeTab(message.tabIndex);
                break;
            default:
                break;
            }
        }
        return null;
    }
    
    public static enum ShopMessageType {
        EDIT_MODE_ON,
        EDIT_MODE_OFF,
        SHOP_ADD,
        SHOP_REMOVE,
        SHOP_EDIT,
        SHOP_CHANGE,
        ITEM_UPDATE,
        TAB_ADD,
        TAB_REMOVE,
        TAB_EDIT,
        TAB_CHANGED
    }
}
