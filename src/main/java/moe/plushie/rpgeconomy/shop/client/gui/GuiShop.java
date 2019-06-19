package moe.plushie.rpgeconomy.shop.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShop.IShopTab;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTab;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiTabbed;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import moe.plushie.rpgeconomy.shop.common.tileentities.TileEntityShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShop extends GuiTabbed {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SHOP);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 152;
    
    private final EntityPlayer entityPlayer;
    private int activeTabIndex = 0;
    private IShop shop;
    private boolean shopLinked = false;
    
    private GuiIconButton buttonEditMode;
    private GuiIconButton buttonShopList;
    private GuiIconButton buttonSave;
    
    public GuiShop(EntityPlayer entityPlayer, TileEntityShop tileEntity) {
        super(new ContainerShop(entityPlayer, tileEntity), false);
        this.entityPlayer = entityPlayer;
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
    }
    
    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 145;
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            this.ySize += 98 + 1;
        }
        super.initGui();
        tabController.x = getGuiLeft() + 4;
        
        buttonEditMode = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 147, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Edit Mode");
        buttonShopList = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 167, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Shop List");
        buttonSave = new GuiIconButton(this, 0, getGuiLeft(), getGuiTop() + 187, 16, 16, TEXTURE).setDrawButtonBackground(true).setHoverText("Save");
        
        buttonList.add(buttonEditMode);
        buttonList.add(buttonShopList);
        buttonList.add(buttonSave);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        
        // Render shop background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21, getGuiTop(), 0, 0, 278, 145, 100, 100, 4, zLevel);
        
        // Render money background.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 177, getGuiTop() + 146, 0, 0, 101, 98, 100, 100, 4, zLevel);
        
        // Render title.
        GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 33, getGuiTop() + 4, 0, 132, 200, 13, 100, 13, 2, zLevel);
        
        // Render item box.
        for (int i = 0; i < 4; i++) {
            GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 4, getGuiTop() + 18 + 31 * i, 0, 101, 134, 30, 134, 30, 2, zLevel);
            GuiUtils.drawContinuousTexturedBox(getGuiLeft() + 21 + 4 + 136, getGuiTop() + 18 + 31 * i, 0, 101, 134, 30, 134, 30, 2, zLevel);
        }
        
        //drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, TEXTURE_SIZE_X, TEXTURE_SIZE_Y);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                //drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }
        }
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft() + 21, getGuiTop() + 145 + 1);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Loading...";
        int titleColour = 0x333333;
        if (shop != null) {
            title = shop.getName();
        }
        if (!shopLinked) {
            title = "SHOP NOT LINKED";
            titleColour = 0xAA0000;
        }
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, titleColour);
        
        if (ConfigHandler.showPlayerInventoryInShopGUI) {
            GuiHelper.renderPlayerInvlabel(21, 145 + 1, fontRenderer);
        }
        
        fontRenderer.drawString("Munie!", 206, 151, 0x333333);
        
        Currency currency = RpgEconomy.getProxy().getCurrencyManager().getCurrency("Common");
        for (CurrencyVariant variant : currency.getCurrencyVariants()) {
            
        }
        
        fontRenderer.drawString("Stock: \u221E", 55, 21, 0x888888, false);
        

        for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.translate(55 + i * 17, 30, 0);
            //GlStateManager.scale(0.5, 0.5, 0.5);
            CurrencyVariant variant = currency.getCurrencyVariants()[i];
            ItemStack stack = variant.getItem().copy();
            stack.setCount(20);
            itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
            itemRender.renderItemOverlays(fontRenderer, stack, 0, 0);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        for (GuiButton button : buttonList) {
            if (button instanceof GuiIconButton) {
                //((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GlStateManager.popMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }
    
    public void gotShopFromServer(IShop shop) {
        this.shop = shop;
        shopLinked = shop != null;
        if (shop != null) {
            for (int i = 0; i < shop.getTabCount(); i++) {
                IShopTab shopTab = shop.getTabs()[i];
                tabController.addTab(new GuiTab(shopTab.getName()).setIconLocation(0, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
            }
        }
        activeTabIndex = 0;
        tabController.setActiveTabIndex(getActiveTab());
        tabChanged();
    }

    @Override
    public String getName() {
        return LibBlockNames.SHOP;
    }

    @Override
    protected int getActiveTab() {
        return activeTabIndex;
    }

    @Override
    protected void setActiveTab(int value) {
        activeTabIndex = value;
    }
}
