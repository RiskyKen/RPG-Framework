package moe.plushie.rpgeconomy.common.network;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.client.gui.mailbox.GuiMailBox;
import moe.plushie.rpgeconomy.common.inventory.ContainerMailBox;
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
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            return new ContainerMailBox(player.inventory);
        }
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            return new GuiMailBox(player.inventory);
        }
        return null;
    }
}
