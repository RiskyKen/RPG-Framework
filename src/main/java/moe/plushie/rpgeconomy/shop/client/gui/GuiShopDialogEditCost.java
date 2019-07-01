package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogEditCost extends AbstractGuiDialog implements IDropDownListCallback {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiDropDownList dropDownCostTypes;
    private GuiDropDownList dropDownCurrencyTypes;

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
        dropDownCurrencyTypes = new GuiDropDownList(0, x + width - 110, y + 25, 100, "", this);

        String[] costTypes = new String[] { "Free", "Currency", "Items" };
        for (String type : costTypes) {
            dropDownCostTypes.addListItem(type);
        }

        CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
        for (int i = 0; i < currencyManager.getCurrencies().length; i++) {
            ICurrency currency = currencyManager.getCurrencies()[i];
            dropDownCurrencyTypes.addListItem(currency.getName(), currency.getIdentifier(), true);
            if (cost != null && cost.hasWalletCost()) {
                if (currency == cost.getWalletCost().getCurrency()) {
                    dropDownCurrencyTypes.setListSelectedIndex(i);
                }
            }
        }

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
        buttonList.add(dropDownCurrencyTypes);
        setVisibleItems();
    }

    private void setVisibleItems() {
        dropDownCurrencyTypes.visible = dropDownCostTypes.getListSelectedIndex() == 1;
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
        dropDownCurrencyTypes.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }

    public ICost getCost() {
        return cost;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        setVisibleItems();
    }
}
