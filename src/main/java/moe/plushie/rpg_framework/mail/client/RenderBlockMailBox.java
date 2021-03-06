package moe.plushie.rpg_framework.mail.client;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.mail.common.blocks.BlockMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMailBox extends TileEntitySpecialRenderer<TileEntityMailBox> {

    private static final ResourceLocation TEXTURE_FLAG = new ResourceLocation(LibModInfo.ID, "textures/blocks/mail_box/mail_box_flag_tesr.png");
    private final ModelMailBoxFlag modelMailBoxFlag = new ModelMailBoxFlag();

    @Override
    public void render(TileEntityMailBox te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = EnumFacing.EAST;
        if (blockState.getBlock() == ModBlocks.MAIL_BOX) {
            facing = blockState.getValue(BlockMailBox.STATE_FACING);
        }
        
        IMailSystem mailSystem = te.getMailSystem();
        if (mailSystem == null) {
            return;
        }
        if (!mailSystem.isMailboxFlagRender()) {
            return;
        }
        
        bindTexture(TEXTURE_FLAG);
        float scale = 0.0625F;
        GlStateManager.pushMatrix();

        GlStateManager.enableNormalize();
        GlStateManager.enableRescaleNormal();

        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5F, 1F, 0.5F);
        switch (facing) {
        case EAST:
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            break;
        case SOUTH:
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            break;
        case NORTH:
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            break;
        default:
            break;
        }
        float angle = 0F;
        if (MailCounter.getUnreadMailCount(mailSystem) > 0) {
            angle = 90F;
        }
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.scale(1F, -1F, -1F);
        
        modelMailBoxFlag.FlagPole.rotateAngleX = (float) Math.toRadians(angle + 90F);
        
        // modelMailBoxFlag.FlagPole.rotateAngleX = (float) Math.toRadians(90);
        modelMailBoxFlag.render(null, 0, 0, 0, 0, 0, scale);
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.popMatrix();

        //drawNameplate(te, mailSystem.getName(), x, y, z, 10);
    }
}
