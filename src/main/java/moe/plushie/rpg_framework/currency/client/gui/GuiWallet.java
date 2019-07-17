package moe.plushie.rpg_framework.currency.client.gui;

import java.io.IOException;

import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.GuiResourceManager;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.gui.json.GuiJsonInfo;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiButton;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import moe.plushie.rpg_framework.currency.common.inventory.ContainerWallet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWallet extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WALLET);
    private static final ResourceLocation GUI_JSON = new ResourceLocation(LibModInfo.ID, "gui/wallet.json");
    
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 97;

    private final GuiJsonInfo guiJson;
    private final EntityPlayer player;
    private final ItemStack walletStack;
    private final Currency currency;
    private final IWallet wallet;

    public GuiWallet(EntityPlayer entityPlayer, Currency currency) {
        super(new ContainerWallet(entityPlayer, currency));
        this.guiJson = GuiResourceManager.getGuiJsonInfo(GUI_JSON);
        this.player = entityPlayer;
        this.walletStack = player.getHeldItemMainhand();
        this.currency = currency;
        this.wallet = CurrencyCapability.get(player).getWallet(currency);
    }

    @Override
    public void initGui() {
        this.xSize = TEXTURE_SIZE_X;
        this.ySize = TEXTURE_SIZE_Y;
        if (ConfigHandler.options.showPlayerInventoryInWalletGUI) {
            this.ySize += 98 + 1;
        }
        super.initGui();
        buttonList.clear();
        int slotSpacing = 1;
        int slotSize = 18;
        
        int halfSizeX = (int) ((float) xSize / 2F);
        int slotCount = currency.getCurrencyVariants().length;
        int slotTotalWidth = (slotSize + slotSpacing) * slotCount - 1;
        int halfSlotTotalWidth = (int) ((float) slotTotalWidth / 2F);
        int startX = halfSizeX - halfSlotTotalWidth - 1;
        
        for (int i = 0; i < slotCount; i++) {
            CurrencyVariant variant = currency.getCurrencyVariants()[i];
            buttonList.add(new GuiIconButton(this, i, getGuiLeft() + startX + (slotSize + slotSpacing) * i, getGuiTop() + 33, 18, 18, TEXTURE)
                    .setIconLocation(0, 220, 18, 18)
                    .setHoverText("+" + String.valueOf(variant.getValue()))
                    .setDrawButtonBackground(false)
                    .setPlayPressSound(false));
            buttonList.add(new GuiIconButton(this, i + slotCount, getGuiLeft() + startX + (slotSize + slotSpacing) * i, getGuiTop() + 73, 18, 18, TEXTURE)
                    .setIconLocation(0, 238, 18, 18)
                    .setHoverText("-" + String.valueOf(variant.getValue()))
                    .setDrawButtonBackground(false)
                    .setPlayPressSound(false));
        }
        
        buttonList.add(new GuiIconButton(this, -1, getGuiLeft() + startX - slotSize - slotSpacing * 2, getGuiTop() + 33, 18, 18, TEXTURE)
                .setIconLocation(0, 202, 18, 18)
                .setHoverText("+*")
                .setDrawButtonBackground(false)
                .setPlayPressSound(false));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        MessageClientGuiButton message = new MessageClientGuiButton().setButtonID(button.id);
        PacketHandler.NETWORK_WRAPPER.sendToServer(message);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, TEXTURE_SIZE_X, TEXTURE_SIZE_Y);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }
        }
        if (ConfigHandler.options.showPlayerInventoryInWalletGUI) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + TEXTURE_SIZE_Y + 1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = ModItems.WALLET.getItemStackDisplayName(walletStack);
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);

        String value = String.format(currency.getDisplayFormat(), wallet.getAmount());
        int valueWidth = fontRenderer.getStringWidth(value);
        fontRenderer.drawString(value, 139 - valueWidth, 21, 0x333333);
        
        if (ConfigHandler.options.showPlayerInventoryInWalletGUI) {
            GuiHelper.renderPlayerInvlabel(0, TEXTURE_SIZE_Y + 1, fontRenderer);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        for (GuiButton button : buttonList) {
            if (button instanceof GuiIconButton) {
                //((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        GlStateManager.popMatrix();
    }
}
