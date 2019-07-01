package moe.plushie.rpgeconomy.api.bank;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;

public interface IBankAccount {
    
    public int getDatabaseId();
    
    public void setDatabaseId(int id);
    
    public IBank getBank();
    
    public int getTabUnlockCount();
    
    public boolean isTabUnlocked(int index);
    
    public IInventory getTab(int index);
    
    public void unlockTab();
    
    public void removeTab(int index);
    
    public void syncToOwner(EntityPlayerMP entityPlayer);
}
