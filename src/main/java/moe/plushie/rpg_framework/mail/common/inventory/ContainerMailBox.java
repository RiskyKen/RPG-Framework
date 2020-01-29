package moe.plushie.rpg_framework.mail.common.inventory;

import java.util.ArrayList;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.inventory.ModTileContainer;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailList;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailResult;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

public class ContainerMailBox extends ModTileContainer<TileEntityMailBox> {

    private final MailSystem mailSystem;
    private boolean synced = false;

    private final InventoryBasic invAttachmentsInput;

    private final ArrayList<Slot> slotsAttachmentsInput;

    public ContainerMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(entityPlayer, tileEntity);
        this.mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));

        invAttachmentsInput = new InventoryBasic("attachmentsInput", false, mailSystem.getMaxAttachments());

        slotsAttachmentsInput = new ArrayList<Slot>();

        addPlayerSlots(8, 167);

        for (int i = 0; i < invAttachmentsInput.getSizeInventory(); i++) {
            int yIndex = MathHelper.floor(i / 3);
            int xIndex = i - (yIndex * 3);
            addSlotToContainerAndList(new SlotHidable(invAttachmentsInput, i, 246 + 18 * xIndex, 167 + 18 * yIndex), slotsAttachmentsInput);
        }

        for (Slot slot : slotsAttachmentsInput) {
            ((SlotHidable) slot).setVisible(true);
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
        // TODO check shit out.
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

    public void onMailResult(EntityPlayerMP entityPlayer, ArrayList<GameProfile> success, ArrayList<GameProfile> failed) {
        if (!success.isEmpty()) {
            for (int i = 0; i < invAttachmentsInput.getSizeInventory(); i++) {
                invAttachmentsInput.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            getSendCost().pay(entityPlayer);
        }
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerMailResult(success, failed), entityPlayer);
    }
    
    public NonNullList<ItemStack> getAttachments() {
        NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
        for (Slot slot : getSlotsAttachmentsInput()) {
            if (slot.getHasStack()) {
                attachments.add(slot.getStack());
            }
        }
        return attachments;
    }
    
    public ICost getSendCost() {
        ICost cost = mailSystem.getMessageCost();
        NonNullList<ItemStack> attachments = getAttachments();
        for (int i = 0; i < attachments.size(); i++) {
            cost = cost.add(mailSystem.getAttachmentCost());
        }
        return cost;
    }
}
