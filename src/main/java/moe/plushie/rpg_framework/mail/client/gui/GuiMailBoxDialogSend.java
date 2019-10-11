package moe.plushie.rpg_framework.mail.client.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox.MailMessageType;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBoxDialogSend extends AbstractGuiDialog {

    private final IMailSystem mailSystem;
    private GuiLabeledTextField textFieldTo;
    private GuiLabeledTextField textFieldSubject;
    private GuiLabeledTextField textFieldBody;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSend;

    public GuiMailBoxDialogSend(GuiScreen parent, IDialogCallback callback, IMailSystem mailSystem) {
        super(parent, "send-mail", callback, 200, 247);
        this.mailSystem = mailSystem;
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

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30 - 90, 80, 20, "Close");
        buttonSend = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30 - 90, 80, 20, "Send");

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
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 90, textureWidth, textureHeight, borderSize, zLevel);

        GuiHelper.renderPlayerInvTexture(x, y + 158);
        textFieldTo.drawTextBox();
        textFieldSubject.drawTextBox();
        textFieldBody.drawTextBox();
        drawTitle("Send Mail");
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        GuiHelper.renderPlayerInvlabel(x, y + 158, fontRenderer);
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
        NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            attachments.add(mc.player.getHeldItemMainhand());
        }
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
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_SEND).setMailMessages(mailMessages.toArray(new MailMessage[mailMessages.size()])));
        return true;
    }
}
