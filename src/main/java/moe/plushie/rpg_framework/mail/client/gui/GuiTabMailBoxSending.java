package moe.plushie.rpg_framework.mail.client.gui;

import java.util.Calendar;
import java.util.Date;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiLabeledTextField;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabPanel;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabMailBoxSending extends GuiTabPanel<GuiMailBox> {
    
    private static final ResourceLocation TEXTURE_SENDING = new ResourceLocation(LibGuiResources.MAIL_BOX_SENDING);
    
    private GuiButtonExt buttonSend;
    private GuiLabeledTextField textFieldTo;
    private GuiLabeledTextField textFieldSubject;
    private GuiLabeledTextField textFieldMessage;
    
    public GuiTabMailBoxSending(int tabId, GuiMailBox parent) {
        super(tabId, parent, false);
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        buttonSend = new GuiButtonExt(0, 118, 121, 46, 16, "Send");
        buttonList.add(buttonSend);
        
        textFieldTo = new GuiLabeledTextField(fontRenderer, x + 13, y + 17, 150, 12);
        textFieldTo.setMaxStringLength(300);
        textFieldTo.setEmptyLabel("To");
        
        textFieldSubject = new GuiLabeledTextField(fontRenderer, x + 13, y + 33, 150, 12);
        textFieldSubject.setMaxStringLength(300);
        textFieldSubject.setEmptyLabel("Subject");
        
        textFieldMessage = new GuiLabeledTextField(fontRenderer, x + 13, y + 49, 150, 68);
        textFieldMessage.setMaxStringLength(300);
        textFieldMessage.setEmptyLabel("Message");
        
        MailSystem mailSystem = parent.getTileEntity().getMailSystem();
        if (mailSystem != null) {
            textFieldMessage.setText(mailSystem.getName());
        }
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        textFieldTo.mouseClicked(mouseX, mouseY, button);
        textFieldSubject.mouseClicked(mouseX, mouseY, button);
        textFieldMessage.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFieldTo.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textFieldSubject.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textFieldMessage.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonSend) {
            if (textFieldTo.getText().trim().isEmpty()) {
                return;
            }
            if (textFieldSubject.getText().trim().isEmpty()) {
                return;
            }
            if (textFieldMessage.getText().trim().isEmpty()) {
                return;
            }
            MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem("main");
            GameProfile sender = mc.player.getGameProfile();
            GameProfile receiver = new GameProfile(null, textFieldTo.getText());
            Date sendDateTime = Calendar.getInstance().getTime();
            String subject = textFieldSubject.getText();
            String message = textFieldMessage.getText();
            
            NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
            if (!mc.player.getHeldItemMainhand().isEmpty()) {
                attachments.add(mc.player.getHeldItemMainhand());
            }
            
            MailMessage mailMessage = new MailMessage(mailSystem, sender, receiver, sendDateTime, subject, message, attachments);
            
            sendMail(mailMessage);
        }
    }
    
    private void sendMail(MailMessage mailMessage) {
        //PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiMailBox(mailMessage));
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(TEXTURE_SENDING);
        drawTexturedModalRect(x, y, 0, 0, width, height);
        textFieldTo.drawTextBox();
        textFieldSubject.drawTextBox();
        textFieldMessage.drawTextBox();
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, width, parent.getName() + ".tab.sending");
        super.drawForegroundLayer(mouseX, mouseY, 0);
    }
} 
