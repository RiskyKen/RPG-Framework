package moe.plushie.rpg_framework.mail.common.inventory;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.inventory.ModTileContainer;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotOutput;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailList;
import moe.plushie.rpg_framework.core.common.utils.UtilItems;
import moe.plushie.rpg_framework.core.database.TableMail;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMailBox extends ModTileContainer<TileEntityMailBox> {

    private final MailSystem mailSystem;
    private boolean synced = false;

    private final InventoryBasic invAttachmentsInput;

    private final ArrayList<Slot> slotsAttachmentsInput;

    public ContainerMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(entityPlayer, tileEntity);
        this.mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));

        invAttachmentsInput = new InventoryBasic("attachmentsInput", false, 8);

        slotsAttachmentsInput = new ArrayList<Slot>();

        addPlayerSlots(8, 167);

        for (int i = 0; i < invAttachmentsInput.getSizeInventory(); i++) {
            addSlotToContainerAndList(new SlotHidable(invAttachmentsInput, i, 290 - 17 * i, 122), slotsAttachmentsInput);
        }

        for (Slot slot : slotsAttachmentsInput) {
            ((SlotHidable) slot).setVisible(false);
        }
    }

    public ArrayList<Slot> getSlotsAttachmentsInput() {
        return slotsAttachmentsInput;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    public void markDirty() {
        synced = false;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!synced) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) listener;
                    ArrayList<MailMessage> mailMessages = TableMail.getMessages(player, mailSystem);
                    MessageServerMailList message = new MessageServerMailList(mailMessages);
                    PacketHandler.NETWORK_WRAPPER.sendTo(message, player);
                }
            }
            synced = true;
        }
    }

    public void onClientSendMailMessages(EntityPlayerMP player, MailMessage[] mailMessages) {
        RPGFramework.getProxy().getMailSystemManager().onClientSendMailMessage(player, mailMessages);
    }

    public void onClientSelectMessage(EntityPlayerMP player, int messageId) {
        ((MailSystem) mailSystem).onClientSelectMessage(player, messageId);
        // updateOutputSlots(messageId);
    }

    public void onClientDeleteMessage(EntityPlayerMP player, int messageId) {
        ((MailSystem) mailSystem).onClientDeleteMessage(player, messageId);
    }

    public void onClientWithdrawItems(EntityPlayerMP player, int messageId) {
        if (messageId != -1) {
            MailMessage mailMessage = TableMail.getMessage(messageId);
            if (mailMessage != null) {
                for (ItemStack stack : mailMessage.getAttachments()) {
                    UtilItems.spawnItemAtEntity(player, stack, true);
                }
                mailMessage.getAttachments().clear();
                TableMail.clearMessageItems(messageId);
            }
        }
    }
}
