package moe.plushie.rpgeconomy.core.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class ModTileContainer<TILETYPE extends TileEntity> extends ModContainer {

    protected final TILETYPE tileEntity;
    private final EntityPlayer entityPlayer;
    
    public ModTileContainer(EntityPlayer entityPlayer, TILETYPE tileEntity) {
        super(entityPlayer.inventory);
        this.tileEntity = tileEntity;
        this.entityPlayer = entityPlayer;
    }
    
    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead & playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
    }
    
    public TILETYPE getTileEntity() {
        return tileEntity;
    }
}
