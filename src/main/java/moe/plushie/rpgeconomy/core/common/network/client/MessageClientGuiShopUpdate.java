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
    private String tabName;
    private int tabIconIndex;
    
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

    public MessageClientGuiShopUpdate setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        return this;
    }
    
    public MessageClientGuiShopUpdate setTabName(String tabName) {
        this.tabName = tabName;
        return this;
    }
    
    public MessageClientGuiShopUpdate setTabIconIndex(int tabIconIndex) {
        this.tabIconIndex = tabIconIndex;
        return this;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        switch (type) {
        case TAB_ADD:
            buf.writeInt(tabIconIndex);
            ByteBufUtils.writeUTF8String(buf, tabName);
            break;
        case TAB_EDIT:
            buf.writeInt(tabIconIndex);
            ByteBufUtils.writeUTF8String(buf, tabName);
            break;
        case TAB_REMOVE:
            buf.writeInt(tabIndex);
            break;
        case TAB_MOVE_UP:
            break;
        case TAB_MOVE_DOWN:
            break;
        case EDIT_MODE_ON:
            break;
        case EDIT_MODE_OFF:
            break;
        case SHOP_CHANGE:
            if (shopIdentifier != null) {
                buf.writeBoolean(true);
                ByteBufUtils.writeUTF8String(buf, shopIdentifier);
            } else {
                buf.writeBoolean(false);
            }
            break;
        case SHOP_SAVE:
            break;
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
        case TAB_ADD:
            tabIconIndex = buf.readInt();
            tabName = ByteBufUtils.readUTF8String(buf);
            break;
        case TAB_EDIT:
            tabIconIndex = buf.readInt();
            tabName = ByteBufUtils.readUTF8String(buf);
            break;
        case TAB_REMOVE:
            tabIndex = buf.readInt();
            break;
        case TAB_MOVE_UP:
            break;
        case TAB_MOVE_DOWN:
            break;
        case EDIT_MODE_ON:
            break;
        case EDIT_MODE_OFF:
            break;
        case SHOP_CHANGE:
            if (buf.readBoolean()) {
                shopIdentifier = ByteBufUtils.readUTF8String(buf);
            }
            break;
        case SHOP_SAVE:
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
            case TAB_ADD:
                containerShop.tabAdd(message.tabIconIndex, message.tabName);
                break;
            case TAB_EDIT:
                containerShop.tabEdit(message.tabIconIndex, message.tabName);
                break;
            case TAB_REMOVE:
                containerShop.tabRemove(message.tabIndex);
                break;
            case TAB_MOVE_UP:
                containerShop.tabMove(true);
                break;
            case TAB_MOVE_DOWN:
                containerShop.tabMove(false);
                break;
            case EDIT_MODE_ON:
                containerShop.setEditMode(true);
                break;
            case EDIT_MODE_OFF:
                containerShop.setEditMode(false);
                break;
            case SHOP_CHANGE:
                containerShop.setShopIdentifier(message.shopIdentifier);
                break;
            case SHOP_SAVE:
                containerShop.saveShop();
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
        SHOP_SAVE,
        ITEM_UPDATE,
        TAB_ADD,
        TAB_REMOVE,
        TAB_EDIT,
        TAB_CHANGED,
        TAB_MOVE_UP,
        TAB_MOVE_DOWN
    }
}
