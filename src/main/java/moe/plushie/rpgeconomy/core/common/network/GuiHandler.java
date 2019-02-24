package moe.plushie.rpgeconomy.core.common.network;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.inventory.ContainerMailBox;
import moe.plushie.rpgeconomy.core.common.inventory.ContainerWallet;
import moe.plushie.rpgeconomy.core.common.items.ItemWallet;
import moe.plushie.rpgeconomy.core.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.currency.client.gui.GuiWallet;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.mail.client.gui.GuiMailBox;
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
        TileEntity te = null;
        BlockPos pos = new BlockPos(x, y, z);

        if (world.isBlockLoaded(pos)) {
            te = world.getTileEntity(pos);
        }

        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            return new ContainerMailBox(player);
        case LibGuiIds.WALLET:
            ItemStack itemStack = player.getHeldItemMainhand();
            Currency currency = ItemWallet.getCurrency(itemStack);
            if (currency != null) {
                return new ContainerWallet(player, currency);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = null;
        BlockPos pos = new BlockPos(x, y, z);

        if (world.isBlockLoaded(pos)) {
            te = world.getTileEntity(pos);
        }

        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            return new GuiMailBox(player);
        case LibGuiIds.WALLET:
            ItemStack itemStack = player.getHeldItemMainhand();
            Currency currency = ItemWallet.getCurrency(itemStack);
            if (currency != null) {
                return new GuiWallet(player, currency); 
            }
        }
        return null;
    }
}
