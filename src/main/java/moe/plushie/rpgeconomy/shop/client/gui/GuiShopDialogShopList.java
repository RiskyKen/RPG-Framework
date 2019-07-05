package moe.plushie.rpgeconomy.shop.client.gui;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList.IGuiListItem;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiScrollbar;
import moe.plushie.rpgeconomy.core.common.IdentifierString;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogShopList extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSet;
    
    private GuiList listShops;
    private GuiScrollbar scrollbar;
    
    private final ArrayList<IGuiListItem> items = new ArrayList<IGuiListItem>();
    
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
        
        listShops = new GuiList(x + 5, y + 15, width - 20, height - 50, 12);
        for (IGuiListItem listItem : items) {
            listShops.addListItem(listItem);
        }
        scrollbar = new GuiScrollbar(-1, x + width - 15, y + 15, 10, height - 50, "", false);
        scrollbar.setSliderMaxValue(listShops.getTotalListHeight() - listShops.getVisibleHeight());
        scrollbar.setAmount(listShops.getSlotHeight());
        
        buttonList.add(buttonClose);
        buttonList.add(buttonSet);
        buttonList.add(scrollbar);
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
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        listShops.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        listShops.mouseMovedOrUp(mouseX, mouseY, lastButtonClicked);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Shop List";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        listShops.setScrollAmount(scrollbar.getValue());
        listShops.drawList(mouseX, mouseY, partialTickTime);
        //drawTitle();
    }
    
    public void gotShopIdentifiersFromServer(IIdentifier[] shopIdentifiers, String[] shopNames) {
        items.clear();
        for (int i = 0; i < shopIdentifiers.length; i++) {
            items.add(new ListItem(shopNames[i], String.valueOf(shopIdentifiers[i].getValue())));
        }
        initGui();
    }
    
    public IIdentifier getSelectedShopIdentifier() {
        IGuiListItem listItem = listShops.getSelectedListEntry();
        if (listItem != null) {
            return new IdentifierString(((ListItem)listItem).getTag());
        } else {
            return null;
        }
    }
    
    public static class ListItem implements IGuiListItem {

        private final String name;
        private final String tag;
        
        public ListItem(String name, String tag) {
            this.name = name;
            this.tag = tag;
        }
        
        @Override
        public void drawListItem(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, boolean selected, int width) {
            int colour = 0xCCCCCC;
            boolean hover = isHovering(fontRenderer, x, y, mouseX, mouseY, width);
            if (hover) {
                colour = 0xFFFFFF;
            }
            if (selected) {
                colour = 0xDDDD00;
            }
            if (selected & hover) {
                colour = 0xFFFF00;
            }
            fontRenderer.drawString(getDisplayName(), x, y, colour);
        }

        @Override
        public boolean mousePressed(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width) {
            return isHovering(fontRenderer, x, y, mouseX, mouseY, width);
        }

        @Override
        public void mouseReleased(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width) {
        }
        
        private boolean isHovering(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int width) {
            return mouseX >= x & mouseY >= y & mouseX <= x + width - 3 & mouseY <= y + 11;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
        
        public String getTag() {
            return tag;
        }
    }
 }
