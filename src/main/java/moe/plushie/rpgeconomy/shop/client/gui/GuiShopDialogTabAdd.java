package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogTabAdd extends AbstractGuiDialog {

    private final static ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonAdd;
    private GuiButtonExt buttonIconPre;
    private GuiButtonExt buttonIconNext;
    private GuiTextField textFieldName;

    private int iconIndex;

    public GuiShopDialogTabAdd(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        super(parent, name, callback, width, height);
        textFieldName = new GuiTextField(0, fontRenderer, 0, 0, width - 20, 12);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonAdd = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Add");
        buttonIconPre = new GuiButtonExt(-1, x + 20 - 10, y + 35, 20, 20, "<");
        buttonIconNext = new GuiButtonExt(-1, x + width - 30, y + 35, 20, 20, ">");
        
        textFieldName.x = x + 10;
        textFieldName.y = y + 18;
        
        buttonList.add(buttonClose);
        buttonList.add(buttonAdd);
        buttonList.add(buttonIconPre);
        buttonList.add(buttonIconNext);
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
            iconIndex = MathHelper.clamp(iconIndex - 1, 0, 255);
        }
        if (button == buttonIconNext) {
            iconIndex = MathHelper.clamp(iconIndex + 1, 0, 255);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textFieldName.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
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
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Add Tab";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        textFieldName.drawTextBox();
        
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
        int iconY = MathHelper.floor(iconIndex / 16);
        int iconX = iconIndex - (y * 16);
        
        String iconText = iconIndex + "/255";
        int textWidth = fontRenderer.getStringWidth(iconText);
        drawTexturedModalRect(x + width / 2 - 8, y + 35, 16 * iconX, 16 * iconY, 16, 16);
        fontRenderer.drawString(iconText, x + width / 2 - textWidth / 2, y + 52, 0x333333);
        // drawTitle();
    }

    public String getTabName() {
        return textFieldName.getText().trim();
    }

    public int getTabIconIndex() {
        return iconIndex;
    }
}
