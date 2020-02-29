package moe.plushie.rpg_framework.core.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class RenderUtils {
    
    private RenderUtils() {
        throw new IllegalAccessError();
    }
    
    public static ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
        ModelResourceLocation mrl = ModelBakery.MODEL_MISSING;
        try {
            ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
            IBakedModel model = mesher.getItemModel(itemStack);

            ModelManager modelManager = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_175617_aL", "modelManager");
            IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = ReflectionHelper.getPrivateValue(ModelManager.class, modelManager, "field_174958_a", "modelRegistry");
            ModelResourceLocation[] keys = modelRegistry.getKeys().toArray(new ModelResourceLocation[modelRegistry.getKeys().size()]);

            for (int i = 0; i < keys.length; i++) {
                if (modelRegistry.getObject(keys[i]) == model) {
                    mrl = keys[i];
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return mrl;
        
    }

}
