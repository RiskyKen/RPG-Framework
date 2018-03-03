package moe.plushie.rpgeconomy.common.network;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import moe.plushie.rpgeconomy.RPGEconomy;
import moe.plushie.rpgeconomy.client.gui.GuiMailBox;
import moe.plushie.rpgeconomy.common.inventory.ContainerMailBox;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    
    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(RPGEconomy.getInstance(), this);
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            if (te instanceof TileEntityMailBox) {
                return new ContainerMailBox(player.inventory, (TileEntityMailBox) te);
            }
            break;
        }
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch (ID) {
        case LibGuiIds.MAIL_BOX:
            if (te instanceof TileEntityMailBox) {
                return new GuiMailBox(player.inventory, (TileEntityMailBox) te);
            }
            break;
        }
        return null;
    }
}
