package moe.plushie.rpgeconomy.shop.client.gui;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpgeconomy.core.client.gui.IDialogCallback;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiList.IGuiListItem;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiScrollbar;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.IdentifierInt;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiShopUpdate.ShopMessageType;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync.SyncType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogShopList extends AbstractGuiDialog {

    private static final ResourceLocation TEXTURE_SHOP = new ResourceLocation(LibGuiResources.SHOP);

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonSet;
    private GuiIconButton buttonEditTabAdd;
    private GuiIconButton buttonEditTabRemove;

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
        buttonEditTabAdd = new GuiIconButton(parent, 0, x + 6, y + height - 30, 16, 16, TEXTURE_SHOP);
        buttonEditTabRemove = new GuiIconButton(parent, 0, x + 24, y + height - 30, 16, 16, TEXTURE_SHOP);

        buttonEditTabAdd.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        buttonEditTabRemove.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);

        buttonEditTabAdd.setHoverText("Add Shop...");
        buttonEditTabRemove.setHoverText("Remove Shop");

        listShops = new GuiList(x + 5, y + 15, width - 20, height - 50, 12);
        for (IGuiListItem listItem : items) {
            listShops.addListItem(listItem);
        }
        scrollbar = new GuiScrollbar(-1, x + width - 15, y + 15, 10, height - 50, "", false);
        scrollbar.setSliderMaxValue(listShops.getTotalListHeight() - listShops.getVisibleHeight());
        scrollbar.setAmount(listShops.getSlotHeight());

        buttonList.add(buttonClose);
        buttonList.add(buttonSet);
        buttonList.add(buttonEditTabAdd);
        buttonList.add(buttonEditTabRemove);
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
        if (button == buttonEditTabAdd) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_ADD).setShopName("New Shop"));
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientRequestSync(SyncType.SHOPS_IDENTIFIERS));
        }
        if (button == buttonEditTabRemove) {
            IGuiListItem listItem = listShops.getSelectedListEntry();
            if (listItem != null) {
                IdentifierInt identifierInt = new IdentifierInt(Integer.parseInt(((ListItem) listItem).getTag()));
                PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.SHOP_REMOVE).setShopIdentifier(identifierInt));
            }
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
        listShops.drawList(mouseX, mouseY, partialTickTime);
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Shop List";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        if (listShops.getTotalListHeight() <= listShops.getVisibleHeight()) {
            listShops.setScrollAmount(0);
        } else {
            listShops.setScrollAmount(scrollbar.getValue());
        }
        // drawTitle();
    }

    public void gotShopIdentifiersFromServer(IIdentifier[] shopIdentifiers, String[] shopNames) {
        items.clear();
        for (int i = 0; i < shopIdentifiers.length; i++) {
            String id = String.valueOf(shopIdentifiers[i].getValue());
            items.add(new ListItem(id + ": " + shopNames[i], id));
        }
        initGui();
    }

    public IIdentifier getSelectedShopIdentifier() {
        IGuiListItem listItem = listShops.getSelectedListEntry();
        if (listItem != null) {
            ListItem item = (ListItem) listItem;
            return new IdentifierInt(Integer.parseInt(item.tag));
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
