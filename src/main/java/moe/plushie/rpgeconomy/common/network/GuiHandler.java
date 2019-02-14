package moe.plushie.rpgeconomy.common.network;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.client.gui.GuiWallet;
import moe.plushie.rpgeconomy.client.gui.mailbox.GuiMailBox;
import moe.plushie.rpgeconomy.common.inventory.ContainerMailBox;
import moe.plushie.rpgeconomy.common.inventory.ContainerWallet;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import net.minecraft.entity.player.EntityPlayer;
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
            return new ContainerWallet(player);
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
            return new GuiWallet(player); 
        }
        return null;
    }
}
