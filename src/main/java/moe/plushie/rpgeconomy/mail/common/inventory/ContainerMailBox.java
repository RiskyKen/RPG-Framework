package moe.plushie.rpgeconomy.mail.common.inventory;

import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerMailBox extends ModTileContainer<TileEntityMailBox> {

    public ContainerMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(entityPlayer, tileEntity);
        addPlayerSlots(8, 142);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
