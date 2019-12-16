package moe.plushie.rpg_framework.shop.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.GuiSlotHandler;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate.ShopMessageType;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiShopDialogEditCostItems extends AbstractGuiDialog {

    private static final ResourceLocation TEXTURE_SHOP = new ResourceLocation(LibGuiResources.SHOP);
    
    private final GuiSlotHandler slotHandler;
    private final int slotIndex;
    
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiIconButton[] buttonsMeta;
    private GuiIconButton[] buttonsNbt;
    
    private IItemMatcher[] cost;
    
    private boolean[] matchMeta;
    private boolean[] matchNbt;
    
    public GuiShopDialogEditCostItems(GuiScreen parent, String name, IDialogCallback callback, int width, int height, int slotIndex, IItemMatcher[] cost) {
        super(parent, name, callback, width, height);
        this.slotHandler = new GuiSlotHandler((GuiContainer) parent);
        this.slotIndex = slotIndex;
        
        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.ITEM_COST_REQUEST).setSlotIndex(slotIndex));
        
        this.cost = cost;
        matchMeta = new boolean[5];
        matchNbt = new boolean[5];
        for (int i = 0; i < matchMeta.length; i++) {
            matchMeta[i] = true;
        }
        
        
        if (cost != null) {
            for (int i = 0; i < cost.length; i++) {
                if (cost[i] instanceof ItemMatcherStack & i < matchMeta.length) {
                    ItemMatcherStack matcherStack = (ItemMatcherStack) cost[i];
                    matchMeta[i] = matcherStack.isMatchMeta();
                    matchNbt[i] = matcherStack.isMatchNBT();
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30 - 98, 80, 20, "Close");
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30 - 98, 80, 20, "Edit");
        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);
        
        
        buttonsMeta = new GuiIconButton[5];
        buttonsNbt = new GuiIconButton[5];
        for (int i = 0; i < 5; i++) {
            buttonsMeta[i] = new GuiIconButton(parent, i, x + 10 + i * 38, y + 40, 18, 18, TEXTURE_SHOP).setDrawButtonBackground(false);
            if (matchMeta[i]) {
                buttonsMeta[i].setIconLocation(208, 0, 16, 16).setHoverText("Matching Meta");
            } else {
                buttonsMeta[i].setIconLocation(208, 16, 16, 16).setHoverText("Don't Matching Meta");
            }
            buttonsNbt[i] = new GuiIconButton(parent, i + 5, x + 10 + i * 38, y + 60, 18, 18, TEXTURE_SHOP).setDrawButtonBackground(false);
            if (matchNbt[i]) {
                buttonsNbt[i].setIconLocation(208, 32, 16, 16).setHoverText("Match NBT");
            } else {
                buttonsNbt[i].setIconLocation(208, 48, 16, 16).setHoverText("Don't Match NBT");
            }
            buttonList.add(buttonsMeta[i]);
            buttonList.add(buttonsNbt[i]);
        }
        
        slotHandler.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonEdit) {
            returnDialogResult(DialogResult.OK);
        }
        if (button.id >=0 & button.id < 5) {
            matchMeta[button.id] = !matchMeta[button.id];
            initGui();
        }
        if (button.id >=5 & button.id < 10) {
            matchNbt[button.id - 5] = !matchNbt[button.id - 5];
            initGui();
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        try {
            updateSlots(false);
            slotHandler.mouseClicked(mouseX, mouseY, button);
            updateSlots(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        updateSlots(false);
        slotHandler.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        updateSlots(true);
    }
    
    @Override
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        updateSlots(false);
        slotHandler.mouseReleased(mouseX, mouseY, button);
        updateSlots(true);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        try {
            updateSlots(false);
            slotHandler.keyTyped(c, keycode);
            updateSlots(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.keyTyped(c, keycode);
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        //drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 98 - 1, textureWidth, textureHeight, borderSize, zLevel);
        
        // Render slots.
        mc.renderEngine.bindTexture(TEXTURE_SHOP);
        for (int i = 0; i < 5; i++) {
            drawTexturedModalRect(x + 10 + i * 38, y + 20, 100, 0, 18, 18);
        }
        
        GuiHelper.renderPlayerInvTexture(x, y + height - 98);
    }
    
    @Override
    protected void updateSlots(boolean restore) {
        ContainerShop containerShop = (ContainerShop) slotHandler.inventorySlots;
        GuiShop gui = (GuiShop) parent;
        if (!restore) {
            ArrayList<Slot> playerSlots = containerShop.getSlotsPlayer();
            int posX = x + 8 - gui.getGuiLeft();
            int posY = y + 138 - gui.getGuiTop();
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerShop.getSlotsShop()) {
                ((SlotHidable)slot).setVisible(false);
            }
            for (Slot slot : containerShop.getSlotsPrice()) {
                ((SlotHidable)slot).setVisible(true);
            }
        } else {
            ArrayList<Slot> playerSlots = containerShop.getSlotsPlayer();
            int posX = 24;
            int posY = 162;
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerShop.getSlotsShop()) {
                ((SlotHidable)slot).setVisible(true);
            }
            for (Slot slot : containerShop.getSlotsPrice()) {
                ((SlotHidable)slot).setVisible(false);
            }
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Edit Cost";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        // drawTitle();
        GuiHelper.renderPlayerInvlabel(x, y + height - 98, fontRenderer);

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
        
        GlStateManager.pushAttrib();
        
        updateSlots(false);
        slotHandler.drawScreen(mouseX, mouseY, partialTickTime);
        updateSlots(true);
        
        GlStateManager.popAttrib();
        
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
        
        RenderHelper.enableGUIStandardItemLighting();
    }

    public IItemMatcher[] getCost() {
        ContainerShop containerShop = (ContainerShop) slotHandler.inventorySlots;
        ArrayList<IItemMatcher> matchers = new ArrayList<IItemMatcher>();
        ArrayList<Slot> slotsPrice = containerShop.getSlotsPrice();
        for (int i = 0; i < slotsPrice.size(); i++) {
            ItemStack stack = slotsPrice.get(i).getStack();
            if (!stack.isEmpty()) {
                matchers.add(new ItemMatcherStack(stack.copy(), matchMeta[i], matchNbt[i]));
            }
        }
        return matchers.toArray(new IItemMatcher[matchers.size()]);
    }
}
