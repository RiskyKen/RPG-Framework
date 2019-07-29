package moe.plushie.rpg_framework.mail.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Mailbox_Flag - andrew0030
 * Created using Tabula 7.0.0
 */
@SideOnly(Side.CLIENT)
public class ModelMailBoxFlag extends ModelBase {
    public ModelRenderer FlagPole;
    public ModelRenderer FlagUpPart;
    public ModelRenderer FlagUpPart_1;
    public ModelRenderer FlagMidPart;
    public ModelRenderer shape7;

    public ModelMailBoxFlag() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shape7 = new ModelRenderer(this, 13, 0);
        this.shape7.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.shape7.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(shape7, -0.5235987755982988F, 0.0F, 0.0F);
        this.FlagUpPart_1 = new ModelRenderer(this, 7, 7);
        this.FlagUpPart_1.setRotationPoint(0.5F, 6.57F, -0.5F);
        this.FlagUpPart_1.addBox(0.0F, -1.0F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(FlagUpPart_1, 3.3875095451957944F, 0.0F, 0.0F);
        this.FlagPole = new ModelRenderer(this, 0, 0);
        this.FlagPole.setRotationPoint(5.0F, 10.6F, -5.5F);
        this.FlagPole.addBox(0.0F, -0.5F, -0.5F, 2, 10, 1, 0.0F);
        this.setRotateAngle(FlagPole, 3.141592653589793F, 0.0F, 0.0F);
        this.FlagMidPart = new ModelRenderer(this, 13, 5);
        this.FlagMidPart.setRotationPoint(0.5F, 7.5F, -0.3F);
        this.FlagMidPart.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(FlagMidPart, -1.5707963267948966F, 0.0F, 0.0F);
        this.FlagUpPart = new ModelRenderer(this, 7, 0);
        this.FlagUpPart.setRotationPoint(0.5F, 9.5F, -0.5F);
        this.FlagUpPart.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(FlagUpPart, 2.8656560988494895F, 0.0F, 0.0F);
        this.FlagUpPart.addChild(this.shape7);
        this.FlagPole.addChild(this.FlagUpPart_1);
        this.FlagPole.addChild(this.FlagMidPart);
        this.FlagPole.addChild(this.FlagUpPart);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.FlagPole.offsetX, this.FlagPole.offsetY, this.FlagPole.offsetZ);
        GlStateManager.translate(this.FlagPole.rotationPointX * f5, this.FlagPole.rotationPointY * f5, this.FlagPole.rotationPointZ * f5);
        GlStateManager.scale(0.2D, 1.0D, 1.0D);
        GlStateManager.translate(-this.FlagPole.offsetX, -this.FlagPole.offsetY, -this.FlagPole.offsetZ);
        GlStateManager.translate(-this.FlagPole.rotationPointX * f5, -this.FlagPole.rotationPointY * f5, -this.FlagPole.rotationPointZ * f5);
        this.FlagPole.render(f5);
        GlStateManager.popMatrix();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
