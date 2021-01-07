package moe.plushie.rpg_framework.bank.client;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.bank.common.inventory.ContainerBank;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTab;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBank extends GuiTabbed<ContainerBank> implements IDialogCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.BANK);
    private static int activeTabIndex = -1;

    private final IBank bank;

    private GuiIconButton buttonAddTab = new GuiIconButton(this, 0, xSize, ySize, 20, 20, TEXTURE);
    private int panelSizeX;
    private int panelSizeY;
    private int unlockedTabs;

    public GuiBank(EntityPlayer player, IBank bank) {
        super(new ContainerBank(player, bank, null), false);
        this.bank = bank;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        panelSizeX = 176;
        panelSizeY = 21;

        if (bank != null) {
            panelSizeX = Math.max(bank.getTabSlotCountWidth() * 18 + 10, panelSizeX);
            panelSizeY += bank.getTabSlotCountHeight() * 18 + 4;
        }

        this.xSize = panelSizeX;
        this.ySize = panelSizeY;

        this.ySize += 98 + 1;
        super.initGui();
        tabController.x = getGuiLeft() - 17;
        tabController.width = xSize + 42;
        addTabs();

        buttonAddTab = new GuiIconButton(this, 0, width / 2 + 89, getGuiTop() + panelSizeY + 1, 16, 16, TEXTURE_BUTTONS);
        buttonAddTab.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        buttonAddTab.setHoverText(GuiHelper.getLocalControlName(getName(), "button.buy_new_tab"));
        buttonList.add(buttonAddTab);
    }

    private void addTabs() {
        int oldActive = getActiveTab();
        tabController.clearTabs();
        if (bank == null) {
            tabController.setActiveTabIndex(-1);
            return;
        }
        if (bank != null & oldActive == -1) {
            oldActive = -1;
        }
        //if (oldActive == -1 and bank.get)
        if (oldActive == -1) {
            //return;
        }
        tabController.setTabsPerSide(bank.getTabMaxCount() / 2);
        int iconIndex = bank.getTabIconIndex();
        int y = MathHelper.floor(iconIndex / 16);
        int x = iconIndex - (y * 16);
        for (int i = 0; i < unlockedTabs + bank.getTabStartingCount(); i++) {
            tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalControlName(getName(), "tab.name", (i + 1))).setIconLocation(x * 16, y * 16).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        }
        tabController.setActiveTabIndex(oldActive);
    }

    @Override
    protected int getActiveTab() {
        return activeTabIndex;
    }

    @Override
    protected void setActiveTab(int value) {
        activeTabIndex = value;
    }

    @Override
    public String getName() {
        return LibBlockNames.BANK;
    }

    @Override
    public void updateScreen() {
        if (getContainer().getUnlockedTabs() != unlockedTabs) {
            unlockedTabs = getContainer().getUnlockedTabs();
            setActiveTab(0);
            addTabs();
        }
        buttonAddTab.visible = unlockedTabs < bank.getTabUnlockableCount();
        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == tabController) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiButton().setButtonID(getActiveTab()));
        }
        if (button == buttonAddTab) {
            openDialog(new GuiBankDialogBuyTab(this, GuiHelper.getLocalControlName(getName(), "dialog.buy_tab"), this, bank, getContainer().getUnlockedTabs()));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);

        // Render shop background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft(), getGuiTop(), 0, 0, panelSizeX, panelSizeY, 100, 100, 4, zLevel);

        String title = GuiHelper.getLocalControlName(getName(), "name.loading");
        if (bank != null & activeTabIndex != -1) {
            title = bank.getName();
        }
        int titleWidth = fontRenderer.getStringWidth(title);

        // Render title box.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + xSize / 2 - titleWidth / 2 - 5, getGuiTop() + 4, 0, 100, titleWidth + 10, 13, 100, 13, 2, zLevel);

        // Render slots.
        if (bank != null) {
            for (int ix = 0; ix < bank.getTabSlotCountWidth(); ix++) {
                for (int iy = 0; iy < bank.getTabSlotCountHeight(); iy++) {
                    drawTexturedModalRect(getGuiLeft() + (xSize / 2) - ((bank.getTabSlotCountWidth() * 18) / 2) + ix * 18, getGuiTop() + 20 + iy * 18, 238, 0, 18, 18);
                }
            }
        }

        // Render player inv.
        GuiHelper.renderPlayerInvTexture(getGuiLeft() + xSize / 2 - 176 / 2, getGuiTop() + panelSizeY + 1);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Render title text.
        String title = GuiHelper.getLocalControlName(getName(), "name.loading");
        if (bank != null & activeTabIndex != -1) {
            title = bank.getName();
        }
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 4210752);

        GuiHelper.renderPlayerInvlabel(xSize / 2 - 176 / 2, panelSizeY + 1, fontRenderer);
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton) button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }

    private String getTitle() {
        if (bank != null) {
            return bank.getName();
        } else {
            return GuiHelper.getLocalControlName(getName(), "invalid_bank");
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.CANCEL) {
            closeDialog();
        }
        if (result == DialogResult.OK) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiButton().setButtonID(-2));
            closeDialog();
        }
    }
}
