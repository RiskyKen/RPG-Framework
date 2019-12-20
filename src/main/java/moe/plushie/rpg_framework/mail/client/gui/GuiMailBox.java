package moe.plushie.rpg_framework.mail.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.ModGuiContainer;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiList;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiList.IGuiListItem;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiMailBox.MailMessageType;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends ModGuiContainer<ContainerMailBox> implements IDialogCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.MAIL_BOX);

    private final TileEntityMailBox tileEntity;
    private final EntityPlayer player;
    private final MailSystem mailSystem;

    private ArrayList<MailMessage> mailMessages;
    private int mailPage = 0;

    private GuiList listMail;
    private GuiIconButton buttonListPre;
    private GuiIconButton buttonListNext;
    private GuiIconButton buttonNewMessage;
    private GuiIconButton buttonMessageReply;
    private GuiIconButton buttonMessageDelete;
    private GuiIconButton buttonMessageWithdrawItems;

    public GuiMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(new ContainerMailBox(tileEntity, entityPlayer));
        this.tileEntity = tileEntity;
        this.player = entityPlayer;
        this.mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));
        this.mailMessages = new ArrayList<MailMessage>();
        
        for (Slot slot : getContainer().getSlotsAttachmentsInput()) {
            ((SlotHidable) slot).setVisible(false);
        }
    }

    @Override
    public String getName() {
        return LibBlockNames.MAIL_BOX;
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 247;
        super.initGui();
        listMail = new GuiList(guiLeft + 5, guiTop + 18, 100, 111, 18);

        buttonListPre = addButton(new GuiIconButton(this, 0, guiLeft + 5, guiTop + 130, 16, 16, TEXTURE_BUTTONS));
        buttonListNext = addButton(new GuiIconButton(this, 0, guiLeft + 23, guiTop + 130, 16, 16, TEXTURE_BUTTONS));
        buttonNewMessage = addButton(new GuiIconButton(this, 0, guiLeft + 88, guiTop + 130, 16, 16, TEXTURE_BUTTONS));
        buttonMessageReply = addButton(new GuiIconButton(this, 0, guiLeft + 276, guiTop + 22, 16, 16, TEXTURE_BUTTONS));
        buttonMessageDelete = addButton(new GuiIconButton(this, 0, guiLeft + 294, guiTop + 22, 16, 16, TEXTURE_BUTTONS));
        buttonMessageWithdrawItems = addButton(new GuiIconButton(this, 0, guiLeft + 295, guiTop + 125, 16, 16, TEXTURE_BUTTONS));

        buttonListPre.setDrawButtonBackground(false).setIconLocation(208, 128, 16, 16);
        buttonListNext.setDrawButtonBackground(false).setIconLocation(208, 112, 16, 16);
        buttonNewMessage.setDrawButtonBackground(false).setIconLocation(160, 224, 16, 16);
        buttonMessageReply.setDrawButtonBackground(false).setIconLocation(160, 240, 16, 16);
        buttonMessageDelete.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);
        buttonMessageWithdrawItems.setDrawButtonBackground(false).setIconLocation(208, 128, 16, 16);

        buttonListPre.setHoverText("Previous Page");
        buttonListNext.setHoverText("Next Page");
        buttonNewMessage.setHoverText("New Message");
        buttonMessageReply.setHoverText("Reply");
        buttonMessageDelete.setHoverText("Delete");
        buttonMessageWithdrawItems.setHoverText("Withdraw Items");

        buttonMessageWithdrawItems.enabled = false;
        
        buttonListPre.enabled = false;
        buttonListNext.enabled = false;
        
        if (!player.capabilities.isCreativeMode) {
            buttonNewMessage.enabled = false;
            buttonNewMessage.setDisableText("This feature is not available yet.");
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (!isDialogOpen()) {
            if (listMail.mouseClicked(mouseX, mouseY, button)) {
                if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
                    GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
                    mailListItem.getMailMessage().setRead(true);
                    MessageClientGuiMailBox message = new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_SELECT);
                    message.setMessageId(mailListItem.getMailMessage().getId());
                    message.setMailSystem(mailSystem);
                    PacketHandler.NETWORK_WRAPPER.sendToServer(message);
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        if (!isDialogOpen()) {
            listMail.mouseMovedOrUp(mouseX, mouseY, lastButtonClicked);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == buttonListPre) {

        }
        if (button == buttonListNext) {

        }
        if (button == buttonNewMessage) {
            openDialog(new GuiMailBoxDialogSend(this, this, mailSystem));
        }
        if (button == buttonMessageReply) {

        }
        if (button == buttonMessageDelete) {
            if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
                GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
                MessageClientGuiMailBox message = new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_DELETE);
                message.setMessageId(mailListItem.getMailMessage().getId()).setMailSystem(mailSystem);
                PacketHandler.NETWORK_WRAPPER.sendToServer(message);
            }
        }
        if (button == buttonMessageWithdrawItems) {
            if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
                GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
                MessageClientGuiMailBox message = new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_WITHDRAW_ITEMS);
                message.setMessageId(mailListItem.getMailMessage().getId()).setMailSystem(mailSystem);
                PacketHandler.NETWORK_WRAPPER.sendToServer(message);
            }
        }
    }

    public TileEntityMailBox getTileEntity() {
        return tileEntity;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        buttonMessageWithdrawItems.enabled = false;
        if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
            GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
            if (!mailListItem.getMailMessage().getAttachments().isEmpty()) {
                buttonMessageWithdrawItems.enabled = true;
            }
        }
        buttonMessageReply.enabled = listMail.getSelectedIndex() != -1;
        buttonMessageDelete.enabled = listMail.getSelectedIndex() != -1;
        
        // TEMP
        buttonMessageReply.enabled = false;
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Render background.
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, xSize, 150, 64, 64, 4, zLevel);

        // Render title.
        GuiUtils.drawContinuousTexturedBox(guiLeft + 10, guiTop + 4, 0, 64, xSize - 20, 13, 64, 13, 2, zLevel);

        GuiHelper.renderPlayerInvTexture(guiLeft, guiTop + 151);

        listMail.drawList(mouseX, mouseY, partialTicks);

        mc.renderEngine.bindTexture(TEXTURE);

        drawTexturedModalRect(guiLeft + 106, guiTop + 18, 0, 0, 208, 126);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, xSize, getName());

        GuiHelper.renderPlayerInvlabel(0, 151, fontRenderer);

        IGuiListItem guiListItem = listMail.getSelectedListEntry();

        String message = "";

        if (guiListItem != null && guiListItem instanceof GuiMailListItem) {
            MailMessage mailMessage = ((GuiMailListItem) guiListItem).getMailMessage();
            message += "From: " + mailMessage.getSender().getName() + "\n";

            message += "To: " + mailMessage.getReceiver().getName() + "\n\n";
            message += "Subject: " + mailMessage.getSubject() + "\n\n";
            message += mailMessage.getMessageText();
        } else {
            message += "Select a mail message.";
        }
        
        if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            RenderHelper.enableGUIStandardItemLighting();
            GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
            if (!mailListItem.getMailMessage().getAttachments().isEmpty()) {
                int itemCount = 0;
                for (ItemStack stack : mailListItem.getMailMessage().getAttachments()) {
                    if (!stack.isEmpty()) {
                        itemRender.renderItemAndEffectIntoGUI(stack, 277 - itemCount * 17, 125);
                        itemRender.renderItemOverlays(fontRenderer, stack, 277 - itemCount * 17, 125);
                        itemCount++;
                    }
                }
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }

        fontRenderer.drawSplitString(message, 112, 24, 200, 0x444444);

        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton) button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }

    public void gotListFromServer(ArrayList<MailMessage> mailMessages) {
        RPGFramework.getLogger().info("Got message list from server.");
        this.mailMessages = mailMessages;
        updateMailList(mailPage);
    }

    public void updateMailList(int page) {
        listMail.clearList();
        for (MailMessage mailMessage : mailMessages) {
            listMail.addListItem(new GuiMailListItem(mailMessage));
        }
    }

    private static class GuiMailListItem extends GuiList.GuiListItem {

        private MailMessage mailMessage;

        public GuiMailListItem(MailMessage mailMessage) {
            super(mailMessage.getSubject());
            this.mailMessage = mailMessage;
        }

        public MailMessage getMailMessage() {
            return mailMessage;
        }

        @Override
        public void drawListItem(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, boolean selected, int width) {
            Minecraft mc = Minecraft.getMinecraft();

            int yoffset = 0;
            if (!mailMessage.getAttachments().isEmpty()) {
                yoffset -= 16;
            }
            if (!mailMessage.isRead()) {
                yoffset -= 32;
            }
            // drawRect(x, y, x + width, y + 18, 0xCCFFFFFF);
            mc.renderEngine.bindTexture(TEXTURE);
            drawModalRectWithCustomSizedTexture(x, y, 0, 240 + yoffset, 16, 16, 256, 256);
            int colour = 0xCCCCCC;
            boolean hover = isHovering(fontRenderer, x, y, mouseX, mouseY, width);
            if (hover) {
                colour = 0xFFFFFF;
            }
            if (selected) {
                colour = 0xDDDD00;
            }
            if (selected & hover) {
                colour = 0xFFFF00;
            }
            fontRenderer.drawString(getDisplayName(), x + 17, y, colour);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.CANCEL) {
            closeDialog();
        }
        if (dialog.getClass() == GuiMailBoxDialogSend.class & result == DialogResult.OK) {
            closeDialog();
        }
    }

    public void onServerMailResult(ArrayList<GameProfile> success, ArrayList<GameProfile> failed) {
        if (isDialogOpen() && dialog instanceof GuiMailBoxDialogSend) {
            ((GuiMailBoxDialogSend)dialog).onServerMailResult(success, failed);
        }
    }
}
