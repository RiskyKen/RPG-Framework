package moe.plushie.rpgeconomy.shop.client.gui;

import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogShopList extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSet;
    
    public GuiShopDialogShopList(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        super(parent, name, callback, width, height);
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientRequestSync(SyncType.SHOPS_IDENTIFIERS));
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonSet = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Set");
        
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
        String title = "Shop List";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        //drawTitle();
    }
}
