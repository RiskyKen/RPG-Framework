package moe.plushie.rpg_economy.common.network;

import moe.plushie.rpg_economy.RPG_Economy;
import moe.plushie.rpg_economy.client.gui.mailbox.GuiMailBox;
import moe.plushie.rpg_economy.common.inventory.ContainerMailBox;
import moe.plushie.rpg_economy.common.lib.LibGuiIds;
import moe.plushie.rpg_economy.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {
    
    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(RPG_Economy.getInstance(), this);
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
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
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
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
