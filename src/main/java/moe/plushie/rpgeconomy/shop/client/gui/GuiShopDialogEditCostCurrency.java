package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyVariant;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
import moe.plushie.rpgeconomy.currency.common.Wallet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogEditCostCurrency extends AbstractGuiDialog implements IDropDownListCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WALLET);

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiDropDownList dropDownCurrencyTypes;

    private IWallet wallet;

    private ICurrency currency;
    private int amount;

    public GuiShopDialogEditCostCurrency(GuiScreen parent, String name, IDialogCallback callback, int width, int height, IWallet wallet) {
        super(parent, name, callback, width, height);
        this.wallet = wallet;
        if (wallet != null) {
            this.currency = wallet.getCurrency();
            this.amount = wallet.getAmount();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Edit");
        dropDownCurrencyTypes = new GuiDropDownList(-1, x + 10, y + 25, 100, "", this);

        dropDownCurrencyTypes.addListItem("None", "", true);
        dropDownCurrencyTypes.setListSelectedIndex(0);
        CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
        for (int i = 0; i < currencyManager.getCurrencies().length; i++) {
            ICurrency currency = currencyManager.getCurrencies()[i];
            dropDownCurrencyTypes.addListItem(currency.getName(), currency.getIdentifier(), true);
            if (this.currency == currency) {
                dropDownCurrencyTypes.setListSelectedIndex(i + 1);
            }
        }

        addWalletButtons(currency);

        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);
        buttonList.add(dropDownCurrencyTypes);
    }

    private void addWalletButtons(ICurrency currency) {
        if (currency == null) {
            return;
        }
        int slotSpacing = 1;
        int slotSize = 18;

        int slotCount = currency.getCurrencyVariants().length;
        int startX = 120;

        for (int i = 0; i < slotCount; i++) {
            ICurrencyVariant variant = currency.getCurrencyVariants()[currency.getCurrencyVariants().length - i - 1];
            buttonList.add(new GuiIconButton(parent, i, x + startX + (slotSize + slotSpacing) * i, y + 33 - 10, 18, 18, TEXTURE).setIconLocation(0, 220, 18, 18).setHoverText("+" + String.valueOf(variant.getValue())).setDrawButtonBackground(false).setPlayPressSound(false));
            buttonList.add(new GuiIconButton(parent, i + slotCount, x + startX + (slotSize + slotSpacing) * i, y + 73 - 10, 18, 18, TEXTURE).setIconLocation(0, 238, 18, 18).setHoverText("-" + String.valueOf(variant.getValue())).setDrawButtonBackground(false).setPlayPressSound(false));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonEdit) {
            returnDialogResult(DialogResult.OK);
        }
        if (button.id >= 0 & currency != null) {
            int id = button.id;
            boolean remove = id >= currency.getCurrencyVariants().length;
            id = id % currency.getCurrencyVariants().length;
            ICurrencyVariant variant = currency.getCurrencyVariants()[currency.getCurrencyVariants().length - id - 1];

            int change = variant.getValue();
            if (remove) {
                change = -change;
            }
            if (GuiScreen.isShiftKeyDown()) {
                change = change * 10;
            }
            
            amount = MathHelper.clamp(amount + change, 0, Integer.MAX_VALUE);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        GlStateManager.pushAttrib();
        if (currency != null) {
            int armoutLeft = amount;
            for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
                ICurrencyVariant variant = currency.getCurrencyVariants()[currency.getCurrencyVariants().length - 1 - i];
                ItemStack stack = variant.getItem().getItemStack();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 122 + 19 * i, y + 44);
                if (armoutLeft > 0) {
                    int count = 0;
                    for (int j = 0; j < 22000; j++) {
                        if (variant.getValue() <= armoutLeft) {
                            armoutLeft -= variant.getValue();
                            count++;
                        } else {
                            continue;
                        }
                    }
                    if (count != 0) {
                        if (count >= 1000) {
                            mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, stack, x + 122 + 19 * i, y + 44, String.valueOf(count / 1000) + "K");
                        } else {
                            mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, stack, x + 122 + 19 * i, y + 44, String.valueOf(count));
                        }
                    }
                }
            }
        }
        GlStateManager.popAttrib();
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Edit Cost";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        // drawTitle();

        GlStateManager.color(1F, 1F, 1F, 1F);
        // mc.renderEngine.bindTexture(ICONS);

        dropDownCurrencyTypes.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }

    public IWallet getWallet() {
        if (currency != null & amount > 0) {
            return new Wallet(currency, amount);
        }
        return null;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
        currency = currencyManager.getCurrency(dropDownList.getListSelectedItem().tag);
        amount = 0;
        initGui();
    }
}
