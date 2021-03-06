package moe.plushie.rpg_framework.core.common.network;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.client.GuiBank;
import moe.plushie.rpg_framework.bank.common.inventory.ContainerBank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.gui.manager.GuiManager;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.inventory.ContainerManager;
import moe.plushie.rpg_framework.core.common.inventory.IGuiFactory;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.currency.client.gui.GuiWallet;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.CurrencyWalletHelper;
import moe.plushie.rpg_framework.currency.common.inventory.ContainerWallet;
import moe.plushie.rpg_framework.loot.client.gui.GuiBasicLootBag;
import moe.plushie.rpg_framework.loot.client.gui.GuiLootEditor;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerBasicLootBag;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerLootEditor;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.shop.client.gui.GuiShop;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import moe.plushie.rpg_framework.stats.client.gui.GuiStats;
import moe.plushie.rpg_framework.stats.common.inventory.ContainerStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(RPGFramework.getInstance(), this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        EnumGuiId guiId = EnumGuiId.values()[ID];

        if (guiId.isTile()) {
            TileEntity te = null;
            BlockPos pos = new BlockPos(x, y, z);
            if (world.isBlockLoaded(pos)) {
                te = world.getTileEntity(pos);
                if (te != null && te instanceof IGuiFactory) {
                    return ((IGuiFactory) te).getServerGuiElement(player, world, pos);
                }
            }
            return null;
        }

        switch (guiId) {
        case MANAGER:
            return new ContainerManager(player);
        case WALLET:
            Currency currency = RPGFramework.getProxy().getCurrencyManager().getCurrencyFromID(x);
            if (currency != null) {
                if (currency.getCurrencyWalletInfo().getNeedItemToAccess()) {
                    if (!CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                        return null;
                    }
                }
                return new ContainerWallet(player, currency);
            }
            break;
        case BANK_COMMAND:
            IBank bank = ModuleBank.getBankManager().getBank(x);
            DBPlayer dbPlayerBank = new DBPlayer(y);
            EntityPlayer targetPlayerBank = player;
            Entity entityBank = world.getEntityByID(z);
            if (entityBank != null && entityBank instanceof EntityPlayer) {
                targetPlayerBank = (EntityPlayer) entityBank;
            }
            if (bank == null) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an invalid bank.");
                return null;
            }
            if (dbPlayerBank.isMissing()) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an bank with invalid source.");
                return null;
            }
            return new ContainerBank(targetPlayerBank, bank, dbPlayerBank);
        case SHOP_COMMAND:
            IIdentifier identifier = new IdentifierInt(x);
            return new ContainerShop(player, identifier, null);
        case LOOT_EDITOR_COMMAND:
            return new ContainerLootEditor(player);
        case BASIC_LOOT_BAG:
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() & stack.getItem() == ModItems.BASIC_LOOT_BAG) {
                return new ContainerBasicLootBag(player, stack);
            }
            break;
        case STATS:
            return new ContainerStats(player);
        case MAIL_COMMAND:
            IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(x);
            DBPlayer dbPlayerMail = new DBPlayer(y);
            EntityPlayer targetPlayer = player;
            Entity entity = world.getEntityByID(z);
            if (entity != null && entity instanceof EntityPlayer) {
                targetPlayer = (EntityPlayer) entity;
            }
            if (mailSystem == null) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an invalid mail system.");
                return null;
            }
            if (dbPlayerMail.isMissing()) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an mail system with invalid source.");
                return null;
            }
            return new ContainerMailBox(targetPlayer, dbPlayerMail, mailSystem);
        default:
            break;
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        EnumGuiId guiId = EnumGuiId.values()[ID];
        if (guiId.isTile()) {
            TileEntity te = null;
            BlockPos pos = new BlockPos(x, y, z);
            if (world.isBlockLoaded(pos)) {
                te = world.getTileEntity(pos);
                if (te != null && te instanceof IGuiFactory) {
                    return ((IGuiFactory) te).getClientGuiElement(player, world, pos);
                }
            }
            return null;
        }

        switch (guiId) {
        case MANAGER:
            return new GuiManager(player);
        case WALLET:
            Currency currency = RPGFramework.getProxy().getCurrencyManager().getCurrencyFromID(x);
            if (currency != null) {
                if (currency.getCurrencyWalletInfo().getNeedItemToAccess()) {
                    if (!CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                        return null;
                    }
                }
                return new GuiWallet(player, currency);
            }
            break;
        case BANK_COMMAND:
            IBank bank = ModuleBank.getBankManager().getBank(x);
            EntityPlayer targetPlayerBank = player;
            if (bank == null) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an invalid bank.");
                return null;
            }
            return new GuiBank(targetPlayerBank, bank);
        case SHOP_COMMAND:
            return new GuiShop(player, false);
        case LOOT_EDITOR_COMMAND:
            return new GuiLootEditor(player);
        case BASIC_LOOT_BAG:
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() & stack.getItem() == ModItems.BASIC_LOOT_BAG) {
                return new GuiBasicLootBag(player, stack);
            }
            break;
        case STATS:
            return new GuiStats(player);
        case MAIL_COMMAND:
            IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(x);
            EntityPlayer targetPlayer = player;
            if (mailSystem == null) {
                RPGFramework.getInstance().getLogger().warn("Tried to open an invalid mail system.");
                return null;
            }
            return new GuiMailBox(targetPlayer, mailSystem);
        default:
            break;
        }
        return null;
    }
}
