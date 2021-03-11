package moe.plushie.rpg_framework.bank.common.inventory;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.api.bank.IBankManager.IBankAccountLoadCallback;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.common.inventory.slot.SlotBank;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiButton.IButtonPress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBank extends ModContainer implements IInventoryChangedListener, IButtonPress, IBankAccountLoadCallback {

    private final EntityPlayer player;
    private final IBank bank;
    private DBPlayer sourcePlayer;
    private IBankAccount bankAccount = null;

    private InventoryBasic inventory;
    private boolean updatingSlots = false;
    private boolean dirty = false;

    private int unlockedTabs = 0;
    private int activeTab = -1;

    public ContainerBank(EntityPlayer player, IBank bank, DBPlayer sourcePlayer) {
        super(player.inventory);
        this.player = player;
        this.bank = bank;
        this.sourcePlayer = sourcePlayer;

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
                    addSlotToContainer(new SlotBank(inventory, x + y * bank.getTabSlotCountWidth(), xOff + 18 * x, posY + y * 18, this));
                }
            }
            // unlockedTabs = bankAccount.getTabUnlockCount();
            setActiveTab(-1);

            if (!isRemote()) {
                if (sourcePlayer != null) {
                    ModuleBank.getBankManager().getBankAccount(this, bank, sourcePlayer);
                } else {
                    ModuleBank.getBankManager().getBankAccount(this, bank, player.getGameProfile());
                }
            }
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
                unlockedTabs = bankAccount.getTabCount();
            }
        }
    }

    private void setBankAccount(IBankAccount bankAccount) {
        this.bankAccount = bankAccount;
        setActiveTab(0);
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
                if (activeTab < 0 | activeTab > bankAccount.getTabCount() - 1) {
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                } else {
                    inventory.setInventorySlotContents(i, bankAccount.getTab(activeTab).getStackInSlot(i));
                }
            }
            updatingSlots = false;
            detectAndSendChanges();
        }
    }

    public IBank getBank() {
        return bank;
    }

    public IBankAccount getBankAccount() {
        return bankAccount;
    }

    public int getActiveTabIndex() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        if (bankAccount != null) {
            this.activeTab = MathHelper.clamp(activeTab, 0, bankAccount.getTabCount());
        } else {
            this.activeTab = -1;
        }
        updateSlotForTab();
    }

    public void setActiveTabClient(int activeTab) {
        if (unlockedTabs < 1) {
            this.activeTab = -1;
        } else {
            this.activeTab = MathHelper.clamp(activeTab, 0, unlockedTabs - 1);
        }
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
                bankAccount.getTab(getActiveTabIndex()).setInventorySlotContents(i, inventory.getStackInSlot(i));
            }
        }
        dirty = true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!isRemote() & dirty & bankAccount != null) {
            RPGFramework.getLogger().info("Saving back account for " + bankAccount.getOwner());
            ModuleBank.getBankManager().saveBankAccount(getBankAccount());
        }
    }

    private void unlockTab() {
        if (unlockedTabs >= bank.getTabUnlockableCount()) {
            return;
        }
        ICost cost = bank.getTabUnlockCost(unlockedTabs);
        if (cost.canAfford(player)) {
            cost.pay(player);
            bankAccount.unlockTab();
            BankAccountSerializer.serializeDatabase(sourcePlayer, bankAccount);
        }
    }

    @Override
    public void buttonPress(int buttonID) {
        if (buttonID == -2) {
            unlockTab();
        } else {
            setActiveTab(buttonID);
        }
    }

    @Override
    public void onBackAccountLoad(IBankAccount bankAccount) {
        if (bankAccount != null) {
            this.sourcePlayer = new DBPlayer(bankAccount.getOwner().getId());
        }
        setBankAccount(bankAccount);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            if (!this.mergeItemStack(stack, 0, getPlayerInvStartIndex(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            slot.onTake(playerIn, stack);
            return result;
        }
        return super.transferStackFromPlayer(playerIn, index);
    }
}
