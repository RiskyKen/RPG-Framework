package moe.plushie.rpg_framework.api.bank;

import moe.plushie.rpg_framework.api.core.IDBPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;

public interface IBankAccount {

    public IBank getBank();

    public IDBPlayer getOwner();

    public int getTabUnlockCount();

    public boolean isTabUnlocked(int index);

    public IInventory getTab(int index);

    public void unlockTab();

    public void removeTab(int index);

    public int getTabCount();

    public void syncToOwner(EntityPlayerMP entityPlayer);
}
