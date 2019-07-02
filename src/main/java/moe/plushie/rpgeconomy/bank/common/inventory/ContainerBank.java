package moe.plushie.rpgeconomy.bank.common.inventory;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.api.bank.IBankCapability;
import moe.plushie.rpgeconomy.bank.common.capability.BankCapability;
import moe.plushie.rpgeconomy.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpgeconomy.core.common.inventory.ModContainer;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotHidable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;

public class ContainerBank extends ModContainer implements IInventoryChangedListener {

    private final EntityPlayer player;
    private final IBank bank;
    private InventoryBasic inventory;
    private int activeTab = 0;
    private boolean updatingSlots = false;

    public ContainerBank(EntityPlayer player, IBank bank, int playerId) {
        super(player.inventory);
        this.player = player;
        this.bank = bank;
        
        int panelSizeX = 176;
        int panelSizeY = 21;

        if (bank != null) {
            panelSizeX = Math.max(bank.getTabSlotCountWidth() * 18 + 10, panelSizeX);
            panelSizeY += bank.getTabSlotCountHeight() * 18 + 4;

            int posX = 8;
            int posY = 21;
            this.inventory = new InventoryBasic("bank", false, bank.getTabSlotCount());
            inventory.addInventoryChangeListener(this);
            for (int y = 0; y < bank.getTabSlotCountHeight(); y++) {
                for (int x = 0; x < bank.getTabSlotCountWidth(); x++) {
                    int xOff = (panelSizeX / 2) - ((bank.getTabSlotCountWidth() * 18) / 2) + 1;
                    addSlotToContainer(new SlotHidable(inventory, x + y * bank.getTabSlotCountWidth(), xOff + 18 * x, posY + y * 18));
                }
            }
            
            updateSlotForTab();
        }

        addPlayerSlots(panelSizeX / 2 - 176 / 2 + 8, panelSizeY + 17);
    }

    private void updateSlotForTab() {
        if (player.getEntityWorld().isRemote) {
            return;
        }
        IBankCapability bankCapability = BankCapability.get(player);
        if (bankCapability != null) {
            updatingSlots = true;
            IBankAccount account = bankCapability.getBank(bank);
            for (int i = 0; i < bank.getTabSlotCount(); i++) {
                inventory.setInventorySlotContents(i, account.getTab(getActiveTab()).getStackInSlot(i));
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
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        if (player.getEntityWorld().isRemote) {
            return;
        }
        if (updatingSlots) {
            return;
        }
        
        IBankCapability bankCapability = BankCapability.get(player);
        if (bankCapability != null) {
            IBankAccount account = bankCapability.getBank(bank);
            for (int i = 0; i < bank.getTabSlotCount(); i++) {
                account.getTab(getActiveTab()).setInventorySlotContents(i, inventory.getStackInSlot(i));
            }
            BankAccountSerializer.serializeDatabase(player, account);
        }
    }
}
