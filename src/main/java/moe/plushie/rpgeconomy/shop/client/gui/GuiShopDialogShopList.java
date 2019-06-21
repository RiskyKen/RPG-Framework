package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogShopList extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSet;
    
    public GuiShopDialogShopList(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        super(parent, name, callback, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "close"));
        buttonSet = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "copy"));
        
        buttonList.add(buttonClose);
        buttonList.add(buttonSet);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonSet) {
            returnDialogResult(DialogResult.OK);
        }
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
    }
}
