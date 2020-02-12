package moe.plushie.rpg_framework.shop.client.gui;

import moe.plushie.rpg_framework.api.shop.IShop.IShopTab;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogTabRemove extends AbstractGuiDialog {

    private final static ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);

    private final IShopTab shopTab;

    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;

    public GuiShopDialogTabRemove(GuiScreen parent, String name, IDialogCallback callback, int width, int height, IShopTab shopTab) {
        super(parent, name, callback, width, height);
        this.shopTab = shopTab;
        this.slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonCancel = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Remove");

        buttonList.add(buttonCancel);
        buttonList.add(buttonOK);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Remove Tab";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        // drawTitle();

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
        int iconY = MathHelper.floor(shopTab.getIconIndex() / 16);
        int iconX = shopTab.getIconIndex() - (y * 16);

        String iconText = shopTab.getName();
        int textWidth = fontRenderer.getStringWidth(iconText);
        drawTexturedModalRect(x + width / 2 - 8, y + 35, 16 * iconX, 16 * iconY, 16, 16);
        fontRenderer.drawString(iconText, x + width / 2 - textWidth / 2, y + 22, 0x333333);
    }
}
