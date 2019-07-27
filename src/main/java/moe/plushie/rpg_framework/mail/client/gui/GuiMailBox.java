package moe.plushie.rpg_framework.mail.client.gui;

import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.ModGuiContainer;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiList;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMailBox extends ModGuiContainer<ContainerMailBox> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.MAIL_BOX);

    private final TileEntityMailBox tileEntity;

    private GuiList listMail;

    public GuiMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(new ContainerMailBox(tileEntity, entityPlayer));
        this.tileEntity = tileEntity;
    }

    @Override
    public String getName() {
        return LibBlockNames.MAIL_BOX;
    }

    @Override
    public void initGui() {
        this.xSize = 320;
        this.ySize = 224;
        super.initGui();
        listMail = new GuiList(guiLeft + 5, guiTop + 18, 100, ySize - 128, 18);
        listMail.addListItem(new GuiList.GuiListItem("Test Item A"));
        listMail.addListItem(new GuiList.GuiListItem("Test Item B"));
    }

    public TileEntityMailBox getTileEntity() {
        return tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Render background.
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, xSize, ySize - 90, 64, 64, 4, zLevel);

        // Render title.
        GuiUtils.drawContinuousTexturedBox(guiLeft + 10, guiTop + 4, 0, 64, xSize - 20, 13, 64, 13, 2, zLevel);

        GuiHelper.renderPlayerInvTexture(guiLeft, guiTop + ySize - 89);

        listMail.drawList(mouseX, mouseY, partialTicks);

        mc.renderEngine.bindTexture(TEXTURE);

        drawTexturedModalRect(guiLeft + 106, guiTop + 18, 0, 0, 208, 126);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderPlayerInvlabel(0, ySize - 89, fontRenderer);

        GuiHelper.renderLocalizedGuiName(fontRenderer, xSize, getName());
    }
}
