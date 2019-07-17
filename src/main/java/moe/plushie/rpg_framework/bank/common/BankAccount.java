package moe.plushie.rpg_framework.bank.common;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncBankAccount;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

public class BankAccount implements IBankAccount {

    private final IBank parentBank;
    private final ArrayList<IInventory> tabs;

    public BankAccount(IBank parentBank) {
        this.parentBank = parentBank;
        this.tabs = new ArrayList<IInventory>();
    }
    
    public void setNewAccount() {
        for (int i = 0; i < parentBank.getTabStartingCount(); i++) {
            unlockTab();
        }
    }

    @Override
    public IBank getBank() {
        return parentBank;
    }

    @Override
    public int getTabUnlockCount() {
        return parentBank.getTabStartingCount() - tabs.size();
    }

    @Override
    public boolean isTabUnlocked(int index) {
        return index < tabs.size();
    }

    @Override
    public IInventory getTab(int index) {
        return tabs.get(index);
    }

    @Override
    public void unlockTab() {
        tabs.add(new InventoryBasic("", false, parentBank.getTabSlotCount()));
    }

    @Override
    public void removeTab(int index) {
        tabs.remove(index);
    }
    
    @Override
    public int getTabCount() {
        return tabs.size();
    }

    @Override
    public void syncToOwner(EntityPlayerMP entityPlayer) {
        if (!entityPlayer.getEntityWorld().isRemote) {
            PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncBankAccount(this), entityPlayer);
        }
    }
}
