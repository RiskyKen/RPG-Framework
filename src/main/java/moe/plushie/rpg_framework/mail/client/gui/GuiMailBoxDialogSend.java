package moe.plushie.rpg_framework.mail.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox.MailMessageType;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBoxDialogSend extends AbstractGuiDialog {

    private final IMailSystem mailSystem;
    private final EntityPlayer player;
    private GuiLabeledTextField textFieldTo;
    private GuiLabeledTextField textFieldSubject;
    private GuiLabeledTextField textFieldBody;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSend;

    public GuiMailBoxDialogSend(GuiScreen parent, String name, IDialogCallback callback, IMailSystem mailSystem, EntityPlayer player) {
        super(parent, name, callback, 200, 247);
        this.mailSystem = mailSystem;
        this.player = player;
        textFieldTo = new GuiLabeledTextField(fontRenderer, x + 10, y + 20, width - 20, 14);
        textFieldSubject = new GuiLabeledTextField(fontRenderer, x + 10, y + 40, width - 20, 14);
        textFieldBody = new GuiLabeledTextField(fontRenderer, x + 10, y + 60, width - 20, 62);

        textFieldTo.setEmptyLabel("To");
        textFieldSubject.setEmptyLabel("Subject");
        textFieldBody.setEmptyLabel("Message");

        textFieldTo.setMaxStringLength(500);
        textFieldBody.setMaxStringLength(500);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 60 - 10, y + height - 30 - 90, 60, 16, I18n.format(LibGuiResources.Controls.BUTTON_CLOSE));
        buttonSend = new GuiButtonExt(-1, x + width - 120 - 15, y + height - 30 - 90, 60, 16, I18n.format(name + ".button.send"));

        buttonList.add(buttonClose);
        buttonList.add(buttonSend);

        textFieldTo.y = y + 20;
        textFieldTo.x = x + 10;
        textFieldTo.width = width - 20;

        textFieldSubject.y = y + 40;
        textFieldSubject.x = x + 10;
        textFieldSubject.width = width - 20;

        textFieldBody.y = y + 60;
        textFieldBody.x = x + 10;
        textFieldBody.width = width - 20;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = false;
        if (textFieldTo.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (textFieldSubject.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (textFieldBody.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        if (!clicked) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFieldTo.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textFieldSubject.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textFieldBody.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonSend) {
            if (sendMail()) {
                buttonSend.enabled = false;
                buttonClose.enabled = false;
                // returnDialogResult(DialogResult.OK);
            }
        }
    }

    @Override
    protected void updateSlots(boolean restore) {
        ContainerMailBox containerMailBox = (ContainerMailBox) slotHandler.inventorySlots;
        GuiMailBox gui = (GuiMailBox) parent;
        if (!restore) {
            ArrayList<Slot> playerSlots = containerMailBox.getSlotsPlayer();
            int posX = x + 8 - gui.getGuiLeft();
            int posY = y + 167 - gui.getGuiTop();
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerMailBox.getSlotsAttachmentsInput()) {
                ((SlotHidable) slot).setVisible(true);
            }
        } else {
            ArrayList<Slot> playerSlots = containerMailBox.getSlotsPlayer();
            int posX = 8;
            int posY = 167;
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerMailBox.getSlotsAttachmentsInput()) {
                ((SlotHidable) slot).setVisible(false);
            }
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        // Main background.
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 97, textureWidth, textureHeight, borderSize, zLevel);

        // Attachments.
        GuiUtils.drawContinuousTexturedBox(x + 177, y + 151, 0, 0, 80, 98, textureWidth, textureHeight, borderSize, zLevel);
        for (int i = 0; i < ((ContainerMailBox) slotHandler.inventorySlots).getSlotsAttachmentsInput().size(); i++) {
            int yIndex = MathHelper.floor(i / 3);
            int xIndex = i - (yIndex * 3);
            drawTexturedModalRect(x + 177 + 8 + xIndex * 18, y + 151 + 15 + 18 * yIndex, 238, 0, 18, 18);
        }

        // Player inventory.
        GuiHelper.renderPlayerInvTexture(x, y + 151);

        textFieldTo.drawTextBox();
        textFieldSubject.drawTextBox();
        textFieldBody.drawTextBox();
        drawTitle();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        GuiHelper.renderPlayerInvlabel(x, y + 151, fontRenderer);
        fontRenderer.drawString(I18n.format(name + ".label.attachments"), x + 177 + 8, y + 151 + 5, 0x333333);
        ICost cost = getSendCost();
        GuiHelper.renderCost(fontRenderer, mc.getRenderItem(), cost, x - 14, y + 120, cost.canAfford(player));
        buttonSend.enabled = cost.canAfford(player);
    }

    public NonNullList<ItemStack> getAttachments() {
        return ((ContainerMailBox) slotHandler.inventorySlots).getAttachments();
    }
    
    public ICost getSendCost() {
        return ((ContainerMailBox) slotHandler.inventorySlots).getSendCost();
    }

    private boolean sendMail() {
        if (textFieldTo.getText().trim().isEmpty()) {
            return false;
        }
        if (textFieldSubject.getText().trim().isEmpty()) {
            return false;
        }

        GameProfile sender = mc.player.getGameProfile();
        Date sendDateTime = Calendar.getInstance().getTime();
        String subject = textFieldSubject.getText();
        String message = textFieldBody.getText();
        NonNullList<ItemStack> attachments = getAttachments();
        String[] split = textFieldTo.getText().trim().split(",");
        ArrayList<MailMessage> mailMessages = new ArrayList<MailMessage>();
        for (String textReceiver : split) {
            GameProfile receiver = null;
            try {
                receiver = new GameProfile(UUID.fromString(textReceiver.trim()), null);
            } catch (Exception e) {

            }
            if (receiver == null) {
                receiver = new GameProfile(null, textReceiver.trim());
            }
            mailMessages.add(new MailMessage(-1, mailSystem, sender, receiver, sendDateTime, subject, message, attachments, false));

        }
        if (mailMessages.isEmpty()) {
            return false;
        }
        if (!player.capabilities.isCreativeMode & mailMessages.size() > 1) {
            return false;
        }

        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_SEND).setMailMessages(mailMessages.toArray(new MailMessage[mailMessages.size()])));
        return true;
    }

    public void onServerMailResult(ArrayList<GameProfile> success, ArrayList<GameProfile> failed) {
        RPGFramework.getLogger().info("success: " + Arrays.toString(success.toArray()));
        RPGFramework.getLogger().info("failed: " + Arrays.toString(failed.toArray()));
        if (success.isEmpty()) {
            buttonSend.enabled = true;
            buttonClose.enabled = true;
        } else {
            returnDialogResult(DialogResult.OK);
        }
    }
}
