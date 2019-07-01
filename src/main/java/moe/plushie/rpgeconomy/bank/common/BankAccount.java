package moe.plushie.rpgeconomy.bank.common;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncBankAccount;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

public class BankAccount implements IBankAccount {

    private final IBank parentBank;
    private final ArrayList<IInventory> tabs;
    private int databaseId;

    public BankAccount(IBank parentBank) {
        this.parentBank = parentBank;
        this.tabs = new ArrayList<IInventory>();
        this.databaseId = -1;
    }
    
    public BankAccount(IBank parentBank, ArrayList<IInventory> tabs, int databaseId) {
        this.parentBank = parentBank;
        this.tabs = tabs;
        this.databaseId = databaseId;
    }
    
    @Override
    public int getDatabaseId() {
        return databaseId;
    }
    
    @Override
    public void setDatabaseId(int id) {
        this.databaseId = id;
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
    public void syncToOwner(EntityPlayerMP entityPlayer) {
        if (!entityPlayer.getEntityWorld().isRemote) {
            PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncBankAccount(this), entityPlayer);
        }
    }
}
