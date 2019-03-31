package moe.plushie.rpgeconomy.shop.common.inventory;

import moe.plushie.rpgeconomy.core.common.inventory.ModTileContainer;
import moe.plushie.rpgeconomy.shop.common.inventory.slot.SlotShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class ContainerShop extends ModTileContainer<TileEntityShop> {

    private final InventoryBasic inventory;
    
    public ContainerShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(entityPlayer, tileEntity);
        
        inventory = new InventoryBasic("shop", false, 4);
        
        inventory.setInventorySlotContents(0, new ItemStack(Items.DIAMOND));
        
        inventory.setInventorySlotContents(1, new ItemStack(Items.DIAMOND_AXE));
        inventory.getStackInSlot(1).addEnchantment(Enchantment.getEnchantmentByLocation("sharpness"), 5);
        
        inventory.setInventorySlotContents(2, new ItemStack(Blocks.DIRT).setStackDisplayName("Dirt Of Protection!!!!"));
        inventory.getStackInSlot(2).addEnchantment(Enchantment.getEnchantmentByLocation("protection"), 10);
        
        inventory.setInventorySlotContents(3, new ItemStack(Items.STICK).setStackDisplayName("Punt Stick!"));
        inventory.getStackInSlot(3).addEnchantment(Enchantment.getEnchantmentByLocation("knockback"), 50);
        
        addPlayerSlots(8, 169);
        
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            addSlotToContainer(new SlotShop(inventory, i, 7, 21 + i * 23));
        }
    }
}
