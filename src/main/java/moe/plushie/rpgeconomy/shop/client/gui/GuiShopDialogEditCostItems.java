package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.api.core.IItemMatcher;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogEditCostItems extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;

    private IItemMatcher[] cost;

    public GuiShopDialogEditCostItems(GuiScreen parent, String name, IDialogCallback callback, int width, int height, IItemMatcher[] cost) {
        super(parent, name, callback, width, height);
        this.cost = cost;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Edit");

        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonEdit) {
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Edit Cost";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        // drawTitle();

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
    }

    public IItemMatcher[] getCost() {
        return cost;
    }
}
