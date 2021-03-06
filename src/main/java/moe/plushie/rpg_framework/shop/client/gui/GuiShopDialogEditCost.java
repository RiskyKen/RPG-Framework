package moe.plushie.rpg_framework.shop.client.gui;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.CostType;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Cost.CostFactory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogEditCost extends AbstractGuiDialog implements IDropDownListCallback {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiDropDownList dropDownCostTypes;
    private GuiButtonExt buttonEditType;

    private final int slotIndex;
    private ICost cost;

    private ICost costNew;

    public GuiShopDialogEditCost(GuiScreen parent, String name, IDialogCallback callback, int width, int height, int index, ICost cost) {
        super(parent, name, callback, width, height);
        this.slotIndex = index;
        this.cost = cost;
        this.costNew = cost;
        if (costNew == null) {
            costNew = Cost.NO_COST;
        }
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, I18n.format(LibGuiResources.Controls.BUTTON_CLOSE));
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, I18n.format(LibGuiResources.Controls.BUTTON_EDIT));
        dropDownCostTypes = new GuiDropDownList(0, x + 10, y + 25, 100, "", this);
        buttonEditType = new GuiButtonExt(-1, x + 120, y + 25, 80, 20, I18n.format(name + ".button.cost_edit"));

        for (CostType costType : CostType.values()) {
            dropDownCostTypes.addListItem(costType.getLocalizedName(), "", !(costType == CostType.ORE_DICTIONARY | costType == CostType.ITEM_VALUE));
        }

        dropDownCostTypes.setListSelectedIndex(0);

        if (cost != null) {
            if (cost.hasWalletCost()) {
                dropDownCostTypes.setListSelectedIndex(1);
            }
            if (cost.hasItemCost()) {
                dropDownCostTypes.setListSelectedIndex(2);
            }
            if (cost.hasOreDictionaryCost()) {
                dropDownCostTypes.setListSelectedIndex(3);
            }
            if (cost.hasItemValueCosts()) {
                dropDownCostTypes.setListSelectedIndex(4);
            }
        }

        buttonEditType.enabled = dropDownCostTypes.getListSelectedIndex() > 0;

        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);
        buttonList.add(dropDownCostTypes);
        buttonList.add(buttonEditType);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonEdit) {
            returnDialogResult(DialogResult.OK);
        }
        if (button == buttonEditType) {
            if (dropDownCostTypes.getListSelectedIndex() == 1) {
                IWallet wallet = null;
                if (costNew != null) {
                    if (costNew.hasWalletCost()) {
                        wallet = costNew.getWalletCosts()[0];
                    }
                }
                openDialog(new GuiShopDialogEditCostCurrency(parent, name + ".dialog.edit_currency", this, 280, 130, wallet));
            }
            if (dropDownCostTypes.getListSelectedIndex() == 2) {
                IItemMatcher[] itemCost = null;
                if (costNew != null) {
                    itemCost = costNew.getItemCosts();
                }
                openDialog(new GuiShopDialogEditCostItems(parent, name + ".dialog.edit_items", this, 190, 220, slotIndex, itemCost));
            }
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
        dropDownCostTypes.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }

    public ICost getCost() {
        return costNew;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        buttonEditType.enabled = dropDownList.getListSelectedIndex() > 0 & dropDownList.getListSelectedIndex() < 3;
        this.costNew = cost;
        if (dropDownList.getListSelectedIndex() == 0) {
            this.costNew = Cost.NO_COST;
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.CANCEL) {
            closeDialog();
        }
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiShopDialogEditCostCurrency) {
                IWallet wallet = ((GuiShopDialogEditCostCurrency) dialog).getWallet();
                costNew = CostFactory.newCost().addWalletCosts(wallet).build();
                closeDialog();
            }
            if (dialog instanceof GuiShopDialogEditCostItems) {
                IItemMatcher[] matchers = ((GuiShopDialogEditCostItems) dialog).getCost();
                costNew = CostFactory.newCost().addItemCosts(matchers).build();
                closeDialog();
            }
        }
    }
}
