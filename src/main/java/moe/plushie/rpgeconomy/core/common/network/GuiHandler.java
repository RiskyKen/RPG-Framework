package moe.plushie.rpgeconomy.core.common.network;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.bank.client.GuiBank;
import moe.plushie.rpgeconomy.bank.common.inventory.ContainerBank;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.manager.GuiManager;
import moe.plushie.rpgeconomy.core.common.IdentifierInt;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.inventory.ContainerManager;
import moe.plushie.rpgeconomy.core.common.inventory.IGuiFactory;
import moe.plushie.rpgeconomy.core.common.lib.EnumGuiId;
import moe.plushie.rpgeconomy.currency.client.gui.GuiWallet;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.CurrencyWalletHelper;
import moe.plushie.rpgeconomy.currency.common.inventory.ContainerWallet;
import moe.plushie.rpgeconomy.loot.client.gui.GuiBasicLootBag;
import moe.plushie.rpgeconomy.loot.client.gui.GuiLootEditor;
import moe.plushie.rpgeconomy.loot.common.inventory.ContainerBasicLootBag;
import moe.plushie.rpgeconomy.loot.common.inventory.ContainerLootEditor;
import moe.plushie.rpgeconomy.shop.client.gui.GuiShop;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(RpgEconomy.getInstance(), this);
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
            Currency currency = RpgEconomy.getProxy().getCurrencyManager().getCurrencyFromID(x);
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
            return new ContainerBank(player, RpgEconomy.getProxy().getBankManager().getBank(x));
        case SHOP_COMMAND:
            IIdentifier identifier = new IdentifierInt(x);
            return new ContainerShop(player, RpgEconomy.getProxy().getShopManager().getShop(identifier), null);
        case LOOT_EDITOR_COMMAND:
            return new ContainerLootEditor(player);
        case BASIC_LOOT_BAG:
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() & stack.getItem() == ModItems.BASIC_LOOT_BAG) {
                return new ContainerBasicLootBag(player, stack);
            }
            break;
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
            Currency currency = RpgEconomy.getProxy().getCurrencyManager().getCurrencyFromID(x);
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
            return new GuiBank(player, RpgEconomy.getProxy().getBankManager().getBank(x));
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
        default:
            break;
        }
        return null;
    }
}
