package moe.plushie.rpg_framework.mail.common.inventory;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.api.mail.IMailSystemManager.IMailSendCallback;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DBPlayerInfo;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailList;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailResult;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.core.common.utils.UtilItems;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Cost.CostFactory;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.itemData.ModuleItemData;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.TableMail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

public class ContainerMailBox extends ModContainer implements IMailSendCallback {

    private final ScriptEngine scriptEngine = new ScriptEngineManager(null).getEngineByName("nashorn");
    private final EntityPlayer targetPlayer;
    private final DBPlayerInfo sourcePlayer;
    private final IMailSystem mailSystem;
    private boolean synced = false;

    private final InventoryBasic invAttachmentsInput;

    private final ArrayList<Slot> slotsAttachmentsInput;

    public ContainerMailBox(EntityPlayer targetPlayer, DBPlayerInfo sourcePlayer, IMailSystem mailSystem) {
        super(targetPlayer.inventory);
        this.targetPlayer = targetPlayer;
        this.sourcePlayer = sourcePlayer;
        this.mailSystem = mailSystem;

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
        scriptEngine.put("getAttachmentCount", (Supplier<Integer>) this::getAttachmentCount);
        scriptEngine.put("getStackSize", (Function<Double, Integer>) this::getStackSize);
        scriptEngine.put("getStackMaxSize", (Function<Double, Integer>) this::getStackMaxSize);
        scriptEngine.put("getStackValue", (Function<Double, Integer>) this::getStackValue);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (!playerIn.getEntityWorld().isRemote) {
            for (int i = 0; i < slotsAttachmentsInput.size(); i++) {
                if (slotsAttachmentsInput.get(i).getHasStack()) {
                    UtilItems.spawnItemAtEntity(getEntityPlayer(), slotsAttachmentsInput.get(i).getStack(), true);
                }
            }
        }
        super.onContainerClosed(playerIn);
    }

    private EntityPlayer getEntityPlayer() {
        return targetPlayer;
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
            EntityPlayerMP player = (EntityPlayerMP) getEntityPlayer();
            ArrayList<MailMessage> mailMessages = TableMail.getMessages(sourcePlayer, mailSystem);
            MessageServerMailList message = new MessageServerMailList(mailMessages);
            PacketHandler.NETWORK_WRAPPER.sendTo(message, player);
            synced = true;
        }
    }

    public void onClientSendMailMessages(EntityPlayerMP player, GameProfile[] receivers, MailMessage mailMessage) {
        // Mail security checks.
        if (!player.capabilities.isCreativeMode) {
            if (!mailSystem.isSendingEnabled()) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message, when sending is disalbed.", getEntityPlayer().getName()));
                return;
            }
            if (!getSendCost().canAfford(getEntityPlayer())) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message they can not afford.", getEntityPlayer().getName()));
                return;
            }
            if (receivers.length != 1) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message to more than one player.", getEntityPlayer().getName()));
                return;
            }
            if (PlayerUtils.gameProfilesMatch(receivers[0], getEntityPlayer().getGameProfile()) & !mailSystem.isAllowSendingToSelf()) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message to themselves, this is not allowed!", getEntityPlayer().getName()));
                return;
            }
            if (mailMessage.getAttachments().size() > mailSystem.getMaxAttachments()) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message with more attachments than is allowed.", getEntityPlayer().getName()));
                return;
            }
            if (mailMessage.getMessageText().length() > mailSystem.getCharacterLimit()) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message, with a body that is over the character limit.", getEntityPlayer().getName()));
                return;
            }
            if (mailMessage.getSubject().length() > MailMessage.SUBJECT_CHARACTER_LIMIT) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message, with a subject that is over the character limit.", getEntityPlayer().getName()));
                return;
            }
            if (receivers[0].getName() != null && receivers[0].getName().startsWith("@")) {
                RPGFramework.getLogger().warn(String.format("User %s tried to send a message, with a special receiver name.", getEntityPlayer().getName()));
                return;
            }
        }
        RPGFramework.getProxy().getMailSystemManager().onSendMailMessages(this, receivers, mailMessage);
    }

    @Override
    public void onMailResult(ArrayList<GameProfile> success, ArrayList<GameProfile> failed) {
        if (!success.isEmpty()) {
            for (int i = 0; i < invAttachmentsInput.getSizeInventory(); i++) {
                invAttachmentsInput.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            getSendCost().pay(getEntityPlayer());
        }
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerMailResult(success, failed), (EntityPlayerMP) getEntityPlayer());
    }

    public void onClientSelectMessage(EntityPlayerMP player, int messageId) {
        ((MailSystem) mailSystem).markMessageasRead(messageId);
        ((MailSystem) mailSystem).notifyClient(player);
    }

    public void onClientDeleteMessage(EntityPlayerMP player, int messageId) {
        ((MailSystem) mailSystem).deleteMessage(messageId);
        ((MailSystem) mailSystem).notifyClient(player);
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
        ICost cost = Cost.NO_COST;

        if (mailSystem.getCurrency() == null) {
            return cost;
        }

        NonNullList<ItemStack> attachments = getAttachments();
        String costAlgorithm = mailSystem.getCostAlgorithm();

        // costAlgorithm = "var result = function() {var value = 0; var i; for (i = 0; i < getAttachmentCount(); i++){ var j; for (j = 0; j < getStackSize(i); j++) { value += 1; }} return value;};";

        costAlgorithm = costAlgorithm.replace("$messageCost", String.valueOf(mailSystem.getMessageCost()));
        costAlgorithm = costAlgorithm.replace("$attachmentCost", String.valueOf(mailSystem.getAttachmentCost()));
        costAlgorithm = costAlgorithm.replace("$attachmentCount", String.valueOf(attachments.size()));

        try {
            scriptEngine.eval(costAlgorithm);
            Invocable inv = (Invocable) scriptEngine;
            Object result = inv.invokeFunction("result");
            Wallet walletCost = new Wallet(mailSystem.getCurrency());
            if (result instanceof Integer) {
                walletCost.setAmount((Integer) result);
            }
            if (result instanceof String) {
                walletCost.setAmount(Integer.parseInt(result.toString()));
            }
            if (result instanceof Double) {
                walletCost.setAmount(MathHelper.ceil((Double) result));
            }
            cost = CostFactory.newCost().addWalletCosts(walletCost).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cost;
    }

    public int getAttachmentCount() {
        return getAttachments().size();
    }

    public int getStackSize(double index) {
        if (index < getAttachments().size()) {
            return getAttachments().get((int) index).getCount();
        }
        return 0;
    }

    public int getStackMaxSize(double index) {
        if (index < getAttachments().size()) {
            return getAttachments().get((int) index).getMaxStackSize();
        }
        return 0;
    }

    public int getStackValue(double index) {
        if (index < getAttachments().size()) {
            ItemStack itemStack = getAttachments().get((int) index);
            IItemData itemData = ModuleItemData.getManager().getItemData(itemStack);
            if (itemData.getValue().hasWalletCost()) {
                return itemData.getValue().getWalletCosts()[0].getAmount();
            }
        }
        return 0;
    }
}
