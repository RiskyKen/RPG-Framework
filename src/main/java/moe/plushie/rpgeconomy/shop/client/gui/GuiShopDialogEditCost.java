package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogEditCost extends AbstractGuiDialog implements IDropDownListCallback {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiDropDownList dropDownCostTypes;
    
    private final int slotIndex;
    private ICost cost;
    
    public GuiShopDialogEditCost(GuiScreen parent, String name, IDialogCallback callback, int width, int height, int index, ICost cost) {
        super(parent, name, callback, width, height);
        this.slotIndex = index;
        this.cost = cost;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Edit");
        dropDownCostTypes = new GuiDropDownList(0, x + 10, y + 25, 100, "", this);
        dropDownCostTypes.addListItem("Free");
        dropDownCostTypes.addListItem("Currency");
        dropDownCostTypes.addListItem("Items");
        
        dropDownCostTypes.setListSelectedIndex(0);
        if (cost != null && cost.hasWalletCost()) {
            dropDownCostTypes.setListSelectedIndex(1);
        }
        if (cost != null && cost.hasItemCost()) {
            dropDownCostTypes.setListSelectedIndex(2);
        }
        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);
        buttonList.add(dropDownCostTypes);
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
        dropDownCostTypes.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        // TODO Auto-generated method stub
        
    }
}
