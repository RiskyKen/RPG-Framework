package moe.plushie.rpg_framework.bank.common.inventory;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.database.DBPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBank extends ModContainer implements IInventoryChangedListener {

    private final EntityPlayer player;
    private final IBank bank;
    private final DBPlayer sourcePlayer;
    private final IBankAccount bankAccount;

    private InventoryBasic inventory;
    private boolean updatingSlots = false;

    private int unlockedTabs = 0;
    private int activeTab = 0;

    public ContainerBank(EntityPlayer player, IBank bank, DBPlayer sourcePlayer) {
        super(player.inventory);
        this.player = player;
        this.bank = bank;
        this.sourcePlayer = sourcePlayer;
        if (sourcePlayer != null) {
            this.bankAccount = BankAccountSerializer.deserializeDatabase(sourcePlayer, getBank());
        } else {
            this.bankAccount = null;
        }

        int panelSizeX = 176;
        int panelSizeY = 21;

        if (bank != null) {
            panelSizeX = Math.max(bank.getTabSlotCountWidth() * 18 + 10, panelSizeX);
            panelSizeY += bank.getTabSlotCountHeight() * 18 + 4;

            int posY = 21;
            this.inventory = new InventoryBasic("bank", false, bank.getTabSlotCount());
            inventory.addInventoryChangeListener(this);
            for (int y = 0; y < bank.getTabSlotCountHeight(); y++) {
                for (int x = 0; x < bank.getTabSlotCountWidth(); x++) {
                    int xOff = (panelSizeX / 2) - ((bank.getTabSlotCountWidth() * 18) / 2) + 1;
                    addSlotToContainer(new SlotHidable(inventory, x + y * bank.getTabSlotCountWidth(), xOff + 18 * x, posY + y * 18));
                }
            }
            setActiveTab(0);
            updateSlotForTab();
        }

        addPlayerSlots(panelSizeX / 2 - 176 / 2 + 8, panelSizeY + 17);
    }
    
    public int getUnlockedTabs() {
        return unlockedTabs;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (bankAccount == null) {
            return;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);
            if (unlockedTabs != bankAccount.getTabCount()) {
                icontainerlistener.sendWindowProperty(this, 0, bankAccount.getTabCount());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            unlockedTabs = data;
        }
    }

    private void updateSlotForTab() {
        if (player.getEntityWorld().isRemote) {
            return;
        }
        if (bankAccount != null) {
            updatingSlots = true;
            for (int i = 0; i < bank.getTabSlotCount(); i++) {
                if (activeTab < 0) {
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                } else {
                    inventory.setInventorySlotContents(i, bankAccount.getTab(getActiveTab()).getStackInSlot(i));
                }
            }
            updatingSlots = false;
        }
    }

    public IBank getBank() {
        return bank;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
        updateSlotForTab();
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        if (player.getEntityWorld().isRemote) {
            return;
        }
        if (updatingSlots) {
            return;
        }
        if (bankAccount != null & sourcePlayer != null) {
            for (int i = 0; i < bank.getTabSlotCount(); i++) {
                bankAccount.getTab(getActiveTab()).setInventorySlotContents(i, inventory.getStackInSlot(i));
            }
            BankAccountSerializer.serializeDatabase(sourcePlayer, bankAccount);
        }
    }
}
