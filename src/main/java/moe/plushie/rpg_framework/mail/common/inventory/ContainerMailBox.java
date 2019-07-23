package moe.plushie.rpg_framework.mail.common.inventory;

import moe.plushie.rpg_framework.core.common.inventory.ModTileContainer;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerMailBox extends ModTileContainer<TileEntityMailBox> {

    public ContainerMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(entityPlayer, tileEntity);
        addPlayerSlots(29, 142);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
