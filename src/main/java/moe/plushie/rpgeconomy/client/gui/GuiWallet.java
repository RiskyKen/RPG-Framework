package moe.plushie.rpgeconomy.client.gui;

import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.common.capability.currency.CurrencyCapability;
import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.init.ModItems;
import moe.plushie.rpgeconomy.common.inventory.ContainerWallet;
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
        this.ySize = 168;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, 176, 68);
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + 70);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = ModItems.WALLET.getItemStackDisplayName(walletStack);
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, xSize / 2 - titleWidth / 2, 6, 0x333333);
        
        GuiHelper.renderPlayerInvlabel(0, 70, fontRenderer);
        
        String value = "£" +  wallet.getAmount();
        int valueWidth = fontRenderer.getStringWidth(value);
        
        fontRenderer.drawString(value, 139 - valueWidth, 25, 0x333333);
    }
}
