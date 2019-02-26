package moe.plushie.rpgeconomy.currency.client.gui;

import java.io.IOException;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.client.gui.GuiHelper;
import moe.plushie.rpgeconomy.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpgeconomy.core.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import moe.plushie.rpgeconomy.currency.common.inventory.ContainerWallet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWallet extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WALLET);

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
        this.xSize = 176;
        this.ySize = 196;
        super.initGui();
        buttonList.clear();
        for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
            buttonList.add(new GuiIconButton(this, i, getGuiLeft() + 33 + 18 * i, getGuiTop() + 33, 18, 18, "in", TEXTURE).setIconLocation(0, 224, 16, 16));
            buttonList.add(new GuiIconButton(this, i + currency.getCurrencyVariants().length, getGuiLeft() + 33 + 18 * i, getGuiTop() + 72, 18, 18, "out", TEXTURE).setIconLocation(0, 240, 16, 16));
        }
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
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + 98);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = ModItems.WALLET.getItemStackDisplayName(walletStack);
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);

        GuiHelper.renderPlayerInvlabel(0, 98, fontRenderer);

        String value = "£" + wallet.getAmount();
        int valueWidth = fontRenderer.getStringWidth(value);

        fontRenderer.drawString(value, 139 - valueWidth, 21, 0x333333);
    }
}
