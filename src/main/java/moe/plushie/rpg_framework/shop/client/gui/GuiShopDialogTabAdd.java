package moe.plushie.rpg_framework.shop.client.gui;

import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogTabAdd extends AbstractGuiDialog implements IDropDownListCallback {

    private final static ResourceLocation TEXTURE_ICONS = new ResourceLocation(LibGuiResources.ICONS);
    private final static ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.BUTTONS);

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonAdd;
    private GuiIconButton buttonIconPre;
    private GuiIconButton buttonIconNext;
    private GuiTextField textFieldName;
    private GuiDropDownList dropDownTabTye;
    private int iconPage = 0;
    private int iconIndex = 0;
    private TabType tabType = TabType.BUY;

    public GuiShopDialogTabAdd(GuiScreen parent, String name, IDialogCallback callback) {
        super(parent, name, callback, 190, 190);
        textFieldName = new GuiTextField(0, fontRenderer, 0, 0, width - 20, 12);
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, I18n.format(LibGuiResources.Controls.BUTTON_CLOSE));
        buttonAdd = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, I18n.format(name + ".button.add"));
        buttonIconPre = new GuiIconButton(parent, -1, x + 10, y + 35, 16, 16, TEXTURE_BUTTONS);
        buttonIconNext = new GuiIconButton(parent, -1, x + width - 16 - 10, y + 35, 16, 16, TEXTURE_BUTTONS);
        dropDownTabTye = new GuiDropDownList(-1, x + 10, y + height - 70, width - 20, "", this);

        buttonIconPre.setDrawButtonBackground(false).setIconLocation(208, 80, 16, 16);
        buttonIconNext.setDrawButtonBackground(false).setIconLocation(208, 96, 16, 16);

        buttonIconPre.setHoverText(I18n.format(LibGuiResources.Controls.BUTTON_PREVIOUS));
        buttonIconNext.setHoverText(I18n.format(LibGuiResources.Controls.BUTTON_NEXT));

        for (TabType tabType : TabType.values()) {
            dropDownTabTye.addListItem(I18n.format("inventory.rpg_economy:common.tab_type." + tabType.toString().toLowerCase()), tabType.toString(), true);
        }
        dropDownTabTye.setListSelectedIndex(0);

        textFieldName.x = x + 10;
        textFieldName.y = y + 18;

        buttonList.add(buttonClose);
        buttonList.add(buttonAdd);
        buttonList.add(buttonIconPre);
        buttonList.add(buttonIconNext);
        buttonList.add(dropDownTabTye);
    }
    
    @Override
    public void update() {
        buttonIconPre.enabled = iconPage > 0;
        buttonIconNext.enabled = iconPage < 7;
        super.update();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonAdd) {
            returnDialogResult(DialogResult.OK);
        }
        if (button == buttonIconPre) {
            iconPage = MathHelper.clamp(iconPage - 1, 0, 7);
        }
        if (button == buttonIconNext) {
            iconPage = MathHelper.clamp(iconPage + 1, 0, 7);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        int clickedIndex = getIconUnderMouse(mouseX, mouseY);
        if (clickedIndex != -1) {
            iconIndex = clickedIndex;
        }
        textFieldName.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFieldName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        textFieldName.drawTextBox();
        drawTitle();
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(TEXTURE_ICONS);
        
        String iconText = (iconPage + 1) + "/8";
        int textWidth = fontRenderer.getStringWidth(iconText);
        for (int iy = 0; iy < 4; iy++) {
            for (int ix = 0; ix < 8; ix++) {
                int icon = (iconPage * 32) + ix + (iy * 8);
                int iconY = MathHelper.floor(icon / 16);
                int iconX = icon - (y * 16);
                int iconNumber = iconPage * 32 + iy * 8 + ix;
                
                int posX = x + width / 2 - (16 * 4) + ix * 16;
                int posY = y + 35 + iy * 16;
                
                drawTexturedModalRect(x + width / 2 - (16 * 4) + ix * 16, y + 35 + iy * 16, 16 * iconX, 16 * iconY, 16, 16);
                if (iconNumber == iconIndex) {
                    int boxColour = 0x44FF0000;
                    drawBox(posX, posY, 16, 16, boxColour);
                }
            }
        }
        int underMouse = getIconUnderMouse(mouseX, mouseY);
        if (underMouse != -1) {
            int gridX = x + width / 2 - (16 * 4);
            int gridY = y + 35;
            int iconX = MathHelper.floor((mouseX - gridX) / 16F);
            int iconY = MathHelper.floor((mouseY - gridY) / 16F);
            drawBox(gridX + iconX * 16, gridY + iconY * 16, 16, 16, 0xCCFFFF00);
        }
        
        super.drawForeground(mouseX, mouseY, partialTickTime);

        fontRenderer.drawString(iconText, x + width / 2 - textWidth / 2, y + 100, 0x333333);
        fontRenderer.drawString(I18n.format(name + ".label.tab_type"), x + 10, y + height - 80, 0x333333);

        dropDownTabTye.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }
    
    private void drawBox(int posX, int posY, int width, int height, int colour) {
        drawRect(posX, posY, posX + 1, posY + height, colour);
        drawRect(posX + 1, posY, posX + width - 1, posY + 1, colour);
        drawRect(posX + 1, posY + height - 1, posX + width - 1, posY + height, colour);
        drawRect(posX + width - 1, posY, posX + width, posY + height, colour);
        GlStateManager.color(1F, 1F, 1F, 1F);
    }
    
    private int getIconUnderMouse(int mouseX, int mouseY) {
        int gridX = x + width / 2 - (16 * 4);
        int gridY = y + 35;
        int gridWidth = 8 * 16;
        int gridHeight = 4 * 16;
        if (mouseX >= gridX & mouseX < gridX + gridWidth) {
            if (mouseY >= gridY & mouseY < gridY + gridHeight) {
                int startOffset = iconPage * 32;
                int iconX = MathHelper.floor((mouseX - gridX) / 16F);
                int iconY = MathHelper.floor((mouseY - gridY) / 16F);
                
                return (iconY * 8) + iconX + startOffset;
            }
        }
        return -1;
    }

    public String getTabName() {
        return textFieldName.getText().trim();
    }

    public int getTabIconIndex() {
        return iconIndex;
    }

    public TabType getTabType() {
        return tabType;
    }

    public float getValuePercentage() {
        return 1F;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        tabType = TabType.valueOf(dropDownList.getListSelectedItem().tag);
    }
}
