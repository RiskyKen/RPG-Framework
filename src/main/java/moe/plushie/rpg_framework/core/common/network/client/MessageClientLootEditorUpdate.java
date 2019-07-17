package moe.plushie.rpg_framework.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.loot.common.LootTableHelper;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerLootEditor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientLootEditorUpdate implements IMessage {

    private LootEditType editType;
    private IIdentifier identifier;
    private String name;
    private String category;
    private ILootTablePool pool;
    private ILootTable table;

    public MessageClientLootEditorUpdate() {
    }

    public MessageClientLootEditorUpdate(LootEditType editType) {
        this.editType = editType;
    }

    public MessageClientLootEditorUpdate setAddData(String name, String category) {
        this.name = name;
        this.category = category;
        return this;
    }

    public MessageClientLootEditorUpdate setRemoveData(IIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public MessageClientLootEditorUpdate setRenameData(IIdentifier identifier, String name, String category) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        return this;
    }

    public MessageClientLootEditorUpdate setEditPoolData(ILootTablePool pool) {
        this.pool = pool;
        return this;

    }

    public MessageClientLootEditorUpdate setEditTableData(ILootTable table) {
        this.table = table;
        return this;
    }
    

    public MessageClientLootEditorUpdate setRequestPoolData(IIdentifier poolIdentifier) {
        this.identifier = poolIdentifier;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(editType.ordinal());
        switch (editType) {
        case LOOT_POOL_ADD:
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, category);
            break;
        case LOOT_POOL_EDIT:
            ByteBufUtils.writeUTF8String(buf, LootTableHelper.poolToJson(pool).toString());
            break;
        case LOOT_POOL_REMOVE:
            ByteBufHelper.writeIdentifier(buf, identifier);
            break;
        case LOOT_POOL_RENAME:
            ByteBufHelper.writeIdentifier(buf, identifier);
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, category);
            break;
        case LOOT_POOL_REQUEST:
            ByteBufHelper.writeIdentifier(buf, identifier);
            break;
        case LOOT_TABLE_ADD:
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, category);
            break;
        case LOOT_TABLE_EDIT:
            ByteBufUtils.writeUTF8String(buf, LootTableHelper.tableToJson(table).toString());
            break;
        case LOOT_TABLE_REMOVE:
            ByteBufHelper.writeIdentifier(buf, identifier);
            break;
        case LOOT_TABLE_RENAME:
            ByteBufHelper.writeIdentifier(buf, identifier);
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, category);
            break;
        case LOOT_TABLE_REQUEST:
            ByteBufHelper.writeIdentifier(buf, identifier);
            break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        editType = LootEditType.values()[buf.readInt()];
        switch (editType) {
        case LOOT_POOL_ADD:
            name = ByteBufUtils.readUTF8String(buf);
            category = ByteBufUtils.readUTF8String(buf);
            break;
        case LOOT_POOL_EDIT:
            pool = LootTableHelper.poolFromJson(ByteBufUtils.readUTF8String(buf));
            break;
        case LOOT_POOL_REMOVE:
            identifier = ByteBufHelper.readIdentifier(buf);
            break;
        case LOOT_POOL_RENAME:
            identifier = ByteBufHelper.readIdentifier(buf);
            name = ByteBufUtils.readUTF8String(buf);
            category = ByteBufUtils.readUTF8String(buf);
            break;
        case LOOT_POOL_REQUEST:
            identifier = ByteBufHelper.readIdentifier(buf);
            break;
        case LOOT_TABLE_ADD:
            name = ByteBufUtils.readUTF8String(buf);
            category = ByteBufUtils.readUTF8String(buf);
            break;
        case LOOT_TABLE_EDIT:
            table = LootTableHelper.tableFromJson(ByteBufUtils.readUTF8String(buf));
            break;
        case LOOT_TABLE_REMOVE:
            identifier = ByteBufHelper.readIdentifier(buf);
            break;
        case LOOT_TABLE_RENAME:
            identifier = ByteBufHelper.readIdentifier(buf);
            name = ByteBufUtils.readUTF8String(buf);
            category = ByteBufUtils.readUTF8String(buf);
            break;
        case LOOT_TABLE_REQUEST:
            identifier = ByteBufHelper.readIdentifier(buf);
            break;
        }
    }

    public static class Handler implements IMessageHandler<MessageClientLootEditorUpdate, IMessage> {

        @Override
        public IMessage onMessage(MessageClientLootEditorUpdate message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (message.editType.getNeedsCreative()) {
                if (!player.capabilities.isCreativeMode) {
                    RpgEconomy.getLogger().warn(String.format("Player %s tried to use the loot edit action %s without creative mode.", player.getName(), message.editType.toString()));
                    return null;
                }
            }
            if (player.openContainer != null && player.openContainer instanceof ContainerLootEditor) {
                ContainerLootEditor container = (ContainerLootEditor) player.openContainer;
                switch (message.editType) {
                case LOOT_POOL_ADD:
                    container.lootPoolAdd(message.name, message.category);
                    break;
                case LOOT_POOL_EDIT:
                    container.lootPoolEdit(message.pool);
                    break;
                case LOOT_POOL_REMOVE:
                    container.lootPoolRemove(message.identifier);
                    break;
                case LOOT_POOL_RENAME:
                    container.lootPoolRename(message.identifier, message.name, message.category);
                    break;
                case LOOT_POOL_REQUEST:
                    container.lootPoolRequest(message.identifier);
                    break;
                case LOOT_TABLE_ADD:
                    break;
                case LOOT_TABLE_EDIT:
                    break;
                case LOOT_TABLE_REMOVE:
                    break;
                case LOOT_TABLE_RENAME:
                    break;
                case LOOT_TABLE_REQUEST:
                    break;
                }
            }
            return null;
        }
    }

    public static enum LootEditType {
        LOOT_POOL_ADD(true),
        LOOT_POOL_EDIT(true),
        LOOT_POOL_REMOVE(true),
        LOOT_POOL_RENAME(true),
        LOOT_POOL_REQUEST(true),
        
        LOOT_TABLE_ADD(true),
        LOOT_TABLE_EDIT(true),
        LOOT_TABLE_REMOVE(true),
        LOOT_TABLE_RENAME(true),
        LOOT_TABLE_REQUEST(true);

        private final boolean needsCreative;

        private LootEditType(boolean needsCreative) {
            this.needsCreative = needsCreative;
        }

        public boolean getNeedsCreative() {
            return needsCreative;
        }
    }
}
