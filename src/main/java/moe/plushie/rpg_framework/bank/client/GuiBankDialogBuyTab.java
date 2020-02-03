package moe.plushie.rpg_framework.bank.client;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiBankDialogBuyTab extends AbstractGuiDialog {

    private final IBank bank;
    private final int unlockedTabs;
    
    private GuiButtonExt buttonCancel;
    private GuiButtonExt buttonOK;
    
    
    public GuiBankDialogBuyTab(GuiScreen parent, String name, IDialogCallback callback, IBank bank, int unlockedTabs) {
        super(parent, name, callback, 190, 100);
        this.bank = bank;
        this.unlockedTabs = unlockedTabs;
        slotHandler = null;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonCancel = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Cancel");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "OK");

        buttonList.add(buttonCancel);
        buttonList.add(buttonOK);
        buttonOK.enabled = bank.getTabUnlockCost(unlockedTabs).canAfford(mc.player);
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
        String title = "Buy Tab";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        
        ICost cost = bank.getTabUnlockCost(unlockedTabs);
        fontRenderer.drawString("Unlock Cost:", x + 10, y + 25, 4210752);
        GuiHelper.renderCost(fontRenderer, mc.getRenderItem(), cost, x - 12, y + 30, cost.canAfford(mc.player));
        // drawTitle();
    }
}
