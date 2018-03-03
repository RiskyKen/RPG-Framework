package moe.plushie.rpgeconomy.common.inventory;

import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class ContainerMailBox extends Container {

    public ContainerMailBox(InventoryPlayer inventoryPlayer, TileEntityMailBox tileEntity) {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
