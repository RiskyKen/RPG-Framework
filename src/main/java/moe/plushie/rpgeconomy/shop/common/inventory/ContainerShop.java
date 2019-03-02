package moe.plushie.rpgeconomy.shop.common.inventory;

import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class ContainerShop extends ModTileContainer<TileEntityShop> {

    private final InventoryBasic inventory;
    
    public ContainerShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(entityPlayer, tileEntity);
        
        inventory = new InventoryBasic("shop", false, 1);
        ItemStack itemStack = new ItemStack(Items.DIAMOND);
        inventory.setInventorySlotContents(0, itemStack);
        
        addPlayerSlots(8, 154);
        
        addSlotToContainer(new SlotShop(inventory, 0, 7, 21));
    }
}
