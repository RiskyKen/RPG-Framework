package moe.plushie.rpg_framework.core.common.network.client;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiShopUpdate implements IMessage, IMessageHandler<MessageClientGuiShopUpdate, IMessage> {

    private ShopMessageType type;
    private IIdentifier shopIdentifier;
    private String shopName;
    private int tabIndex;
    private String tabName;
    private int tabIconIndex;
    private ICost cost;
    private int slotIndex;
    private TabType tabType;
    private float valuePercentage;

    public MessageClientGuiShopUpdate() {
    }

    public MessageClientGuiShopUpdate(ShopMessageType type) {
        this.type = type;
    }

    public MessageClientGuiShopUpdate setShopIdentifier(IIdentifier shopIdentifier) {
        this.shopIdentifier = shopIdentifier;
        return this;
    }

    public MessageClientGuiShopUpdate setShopName(String shopName) {
        this.shopName = shopName;
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

    public MessageClientGuiShopUpdate setCost(int slotIndex, ICost cost) {
        this.slotIndex = slotIndex;
        this.cost = cost;
        return this;
    }

    public MessageClientGuiShopUpdate setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
        return this;
    }

    public MessageClientGuiShopUpdate setTabType(TabType tabType) {
        this.tabType = tabType;
        return this;
    }

    public MessageClientGuiShopUpdate setValuePercentage(float valuePercentage) {
        this.valuePercentage = valuePercentage;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        switch (type) {
        case TAB_ADD:
            buf.writeInt(tabIconIndex);
            ByteBufUtils.writeUTF8String(buf, tabName);
            ByteBufUtils.writeUTF8String(buf, tabType.toString());
            buf.writeFloat(valuePercentage);
            break;
        case TAB_EDIT:
            buf.writeInt(tabIconIndex);
            ByteBufUtils.writeUTF8String(buf, tabName);
            ByteBufUtils.writeUTF8String(buf, tabType.toString());
            buf.writeFloat(valuePercentage);
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
                ByteBufHelper.writeIdentifier(buf, shopIdentifier);
            } else {
                buf.writeBoolean(false);
            }
            break;
        case SHOP_SAVE:
            break;
        case SHOP_RENAME:
            ByteBufUtils.writeUTF8String(buf, shopName);
            break;
        case TAB_CHANGED:
            buf.writeInt(tabIndex);
            break;
        case ITEM_UPDATE:
            buf.writeInt(slotIndex);
            ByteBufUtils.writeUTF8String(buf, CostSerializer.serializeJson(cost, true).toString());
            break;
        case ITEM_COST_REQUEST:
            buf.writeInt(slotIndex);
            break;
        case SHOP_ADD:
            ByteBufUtils.writeUTF8String(buf, shopName);
            break;
        case SHOP_REMOVE:
            ByteBufHelper.writeIdentifier(buf, shopIdentifier);
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
            tabType = TabType.valueOf(ByteBufUtils.readUTF8String(buf));
            valuePercentage = buf.readFloat();
            break;
        case TAB_EDIT:
            tabIconIndex = buf.readInt();
            tabName = ByteBufUtils.readUTF8String(buf);
            tabType = TabType.valueOf(ByteBufUtils.readUTF8String(buf));
            valuePercentage = buf.readFloat();
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
                shopIdentifier = ByteBufHelper.readIdentifier(buf);
            }
            break;
        case SHOP_SAVE:
            break;
        case SHOP_RENAME:
            shopName = ByteBufUtils.readUTF8String(buf);
            break;
        case TAB_CHANGED:
            tabIndex = buf.readInt();
            break;
        case ITEM_UPDATE:
            slotIndex = buf.readInt();
            JsonElement json = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
            cost = CostSerializer.deserializeJson(json);
            break;
        case ITEM_COST_REQUEST:
            slotIndex = buf.readInt();
            break;
        case SHOP_ADD:
            shopName = ByteBufUtils.readUTF8String(buf);
            break;
        case SHOP_REMOVE:
            shopIdentifier = ByteBufHelper.readIdentifier(buf);
            break;
        default:
            break;
        }
    }

    @Override
    public IMessage onMessage(MessageClientGuiShopUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (message.type.getNeedsCreative()) {
            if (!player.capabilities.isCreativeMode) {
                RPGFramework.getLogger().warn(String.format("Player %s tried to use the shop action %s without creative mode.", player.getName(), message.type.toString()));
                return null;
            }
        }
        if (player.openContainer != null && player.openContainer instanceof ContainerShop) {
            ContainerShop containerShop = (ContainerShop) player.openContainer;
            switch (message.type) {
            case TAB_ADD:
                containerShop.tabAdd(message.tabIconIndex, message.tabName, message.tabType, message.valuePercentage);
                break;
            case TAB_EDIT:
                containerShop.tabEdit(message.tabIconIndex, message.tabName, message.tabType, message.valuePercentage);
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
            case SHOP_RENAME:
                containerShop.shopRename(message.shopName);
                break;
            case TAB_CHANGED:
                containerShop.changeTab(message.tabIndex);
                break;
            case ITEM_UPDATE:
                containerShop.updateItem(message.slotIndex, message.cost);
                break;
            case ITEM_COST_REQUEST:
                containerShop.gotCostRequest(message.slotIndex);
                break;
            case SHOP_ADD:
                containerShop.addShop(message.shopName);
                break;
            case SHOP_REMOVE:
                containerShop.removeShop(message.shopIdentifier);
                break;
            default:
                break;
            }
        }
        return null;
    }

    public static enum ShopMessageType {
        /** Edit mode turned on. */
        EDIT_MODE_ON(true),
        /** Edit mode turned off. */
        EDIT_MODE_OFF(true),
        /** Adds a new shop. */
        SHOP_ADD(true),
        // * Removes a shop. */
        SHOP_REMOVE(true),
        /** Shop renamed. */
        SHOP_RENAME(true),
        /** Change linked shop. */
        SHOP_CHANGE(true),
        /** Force shop save. */
        SHOP_SAVE(true), ITEM_UPDATE(true), ITEM_COST_REQUEST(true), TAB_ADD(true), TAB_REMOVE(true), TAB_EDIT(true), TAB_CHANGED(false), TAB_MOVE_UP(true), TAB_MOVE_DOWN(true);

        private final boolean needsCreative;

        private ShopMessageType(boolean needsCreative) {
            this.needsCreative = needsCreative;
        }

        public boolean getNeedsCreative() {
            return needsCreative;
        }
    }
}
