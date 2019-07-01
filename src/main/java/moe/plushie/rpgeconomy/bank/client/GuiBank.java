package moe.plushie.rpgeconomy.bank.client;

import com.mojang.realmsclient.gui.ChatFormatting;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.bank.common.inventory.ContainerBank;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBank extends GuiTabbed {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.BANK);

    private static int activeTabIndex = 0;
    private final EntityPlayer player;
    private final IBank bank;

    private int panelSizeX;
    private int panelSizeY;

    public GuiBank(EntityPlayer player, IBank bank) {
        super(new ContainerBank(player, bank), false);
        this.player = player;
        this.bank = bank;
    }

    @Override
    public void initGui() {
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
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);

        // Render shop background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft(), getGuiTop(), 0, 0, panelSizeX, panelSizeY, 100, 100, 4, zLevel);

        String title = ChatFormatting.RED + "Error Invalid Bank";
        if (bank != null) {
            title = bank.getName();
        }
        int titleWidth = fontRenderer.getStringWidth(title);

        // Render title.
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
        String title = ChatFormatting.RED + "Error Invalid Bank";
        if (bank != null) {
            title = bank.getName();
        }
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);

        GuiHelper.renderPlayerInvlabel(xSize / 2 - 176 / 2, panelSizeY + 1, fontRenderer);
    }
}
