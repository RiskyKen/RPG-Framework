package moe.plushie.rpg_framework.mail.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.ModGuiContainer;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiList;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiList.IGuiListItem;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.IdentifierString;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends ModGuiContainer<ContainerMailBox> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.MAIL_BOX);

    private final TileEntityMailBox tileEntity;

    private ArrayList<MailMessage> mailMessages;
    private int mailPage = 0;
    
    private GuiList listMail;
    private GuiIconButton buttonListPre;
    private GuiIconButton buttonListNext;
    private GuiIconButton buttonNewMessage;
    private GuiIconButton buttonMessageReply;
    private GuiIconButton buttonMessageDelete;

    public GuiMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(new ContainerMailBox(tileEntity, entityPlayer));
        this.tileEntity = tileEntity;
        this.mailMessages = new ArrayList<MailMessage>();
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

        buttonListPre.setDrawButtonBackground(false).setIconLocation(208, 128, 16, 16);
        buttonListNext.setDrawButtonBackground(false).setIconLocation(208, 112, 16, 16);

        buttonNewMessage.setDrawButtonBackground(false).setIconLocation(160, 224, 16, 16);

        buttonMessageReply.setDrawButtonBackground(false).setIconLocation(160, 240, 16, 16);
        buttonMessageDelete.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);

        buttonListPre.setHoverText("Previous Page");
        buttonListNext.setHoverText("Next Page");

        buttonNewMessage.setHoverText("New Message");

        buttonMessageReply.setHoverText("Reply");
        buttonMessageDelete.setHoverText("Delete");

        // Test data
        // listMail.addListItem(new GuiMailListItem("Test Message A"));
        // listMail.addListItem(new GuiMailListItem("Test Item A"));
        // listMail.addListItem(new GuiMailListItem("Test Message B"));
        // listMail.addListItem(new GuiMailListItem("Test Item B"));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (listMail.mouseClicked(mouseX, mouseY, button)) {
            
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        listMail.mouseMovedOrUp(mouseX, mouseY, lastButtonClicked);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == buttonListPre) {
            
        }
        if (button == buttonListNext) {
            
        }
        if (button == buttonNewMessage) {
            sendMail();
        }
        if (button == buttonMessageReply) {
            
        }
        if (button == buttonMessageDelete) {
            if (listMail.getSelectedListEntry() != null && listMail.getSelectedListEntry() instanceof GuiMailListItem) {
                MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));
                GuiMailListItem mailListItem = (GuiMailListItem) listMail.getSelectedListEntry();
                MessageClientGuiMailBox message = new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_DELETE);
                message.setMessageId(mailListItem.getMailMessage().getId()).setMailSystem(mailSystem);
                PacketHandler.NETWORK_WRAPPER.sendToServer(message);
            }
        }
    }

    private void sendMail() {
        MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));
        GameProfile sender = mc.player.getGameProfile();
        GameProfile receiver = mc.player.getGameProfile();
        Date sendDateTime = Calendar.getInstance().getTime();
        String subject = "Test Message";
        String message = "This message is a test. Have a nice day.";

        NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            attachments.add(mc.player.getHeldItemMainhand());
        }
        MailMessage mailMessage = new MailMessage(-1, mailSystem, sender, receiver, sendDateTime, subject, message, attachments, false);
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiMailBox(MailMessageType.MAIL_MESSAGE_SEND).setMailMessage(mailMessage));
    }

    public TileEntityMailBox getTileEntity() {
        return tileEntity;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        buttonMessageReply.enabled = listMail.getSelectedIndex() != -1;
        buttonMessageDelete.enabled = listMail.getSelectedIndex() != -1;
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
        RpgEconomy.getLogger().info("Got message list from server.");
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
            //drawRect(x, y, x + width, y + 18, 0xCCFFFFFF);
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
}
