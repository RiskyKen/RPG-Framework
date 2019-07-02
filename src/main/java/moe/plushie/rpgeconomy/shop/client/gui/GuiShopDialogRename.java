package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogRename extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonOK;
    private GuiTextField textFieldName;

    public GuiShopDialogRename(GuiScreen parent, String name, IDialogCallback callback, int width, int height, String shopName) {
        super(parent, name, callback, width, height);
        textFieldName = new GuiTextField(0, fontRenderer, 0, 0, width - 20, 12);
        textFieldName.setText(shopName);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "OK");

        textFieldName.x = x + 10;
        textFieldName.y = y + 18;

        buttonList.add(buttonClose);
        buttonList.add(buttonOK);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
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
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Rename Shop";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        textFieldName.drawTextBox();
        // drawTitle();
    }

    public String getShopName() {
        return textFieldName.getText().trim();
    }
}
