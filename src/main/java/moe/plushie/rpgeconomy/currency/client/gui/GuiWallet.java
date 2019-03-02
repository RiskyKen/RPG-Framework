package moe.plushie.rpgeconomy.currency.client.gui;

import java.io.IOException;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import moe.plushie.rpgeconomy.currency.common.inventory.ContainerWallet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWallet extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WALLET);
    private static final int TEXTURE_SIZE_X = 176;
    private static final int TEXTURE_SIZE_Y = 97;

    private final ItemStack walletStack;
    private final Currency currency;
    private final IWallet wallet;

    public GuiWallet(EntityPlayer entityPlayer, Currency currency) {
        super(new ContainerWallet(entityPlayer, currency));
        this.walletStack = entityPlayer.getHeldItemMainhand();
        this.currency = currency;
        this.wallet = CurrencyCapability.get(entityPlayer).getWallet(currency);
    }

    @Override
    public void initGui() {
        this.xSize = TEXTURE_SIZE_X;
        this.ySize = TEXTURE_SIZE_Y;
        if (ConfigHandler.showPlayerInventoryInWalletGUI) {
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
            buttonList.add(new GuiIconButton(this, i, getGuiLeft() + startX + (slotSize + slotSpacing) * i, getGuiTop() + 33, 18, 18, TEXTURE)
                    .setIconLocation(0, 220, 18, 18)
                    .setHoverText("in")
                    .setDrawButtonBackground(false));
            buttonList.add(new GuiIconButton(this, i + slotCount, getGuiLeft() + startX + (slotSize + slotSpacing) * i, getGuiTop() + 73, 18, 18, TEXTURE)
                    .setIconLocation(0, 238, 18, 18)
                    .setHoverText("out")
                    .setDrawButtonBackground(false));
        }
        
        buttonList.add(new GuiIconButton(this, -1, getGuiLeft() + startX - slotSize - slotSpacing * 2, getGuiTop() + 33, 18, 18, TEXTURE)
                .setIconLocation(0, 202, 18, 18)
                .setHoverText("all"));
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
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, 176, 97);
        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof SlotCurrency) {
                drawTexturedModalRect(getGuiLeft() + slot.xPos - 1, getGuiTop() + slot.yPos - 1, 238, 0, 18, 18);
            }

        }
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + 98);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = ModItems.WALLET.getItemStackDisplayName(walletStack);
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);

        GuiHelper.renderPlayerInvlabel(0, 98, fontRenderer);
        String value = String.format(currency.getDisplayFormat(), wallet.getAmount());
        int valueWidth = fontRenderer.getStringWidth(value);

        fontRenderer.drawString(value, 139 - valueWidth, 21, 0x333333);
    }
}
