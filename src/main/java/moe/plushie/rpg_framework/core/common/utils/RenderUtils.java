package moe.plushie.rpg_framework.core.common.utils;

import java.util.HashMap;

import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class RenderUtils {

    private static final HashMap<NBTTagCompound, ModelResourceLocation> ITEM_STACK_MODEL_CACHE = new HashMap<NBTTagCompound, ModelResourceLocation>();

    private RenderUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
        NBTTagCompound compound = itemStack.writeToNBT(new NBTTagCompound());
        if (!ITEM_STACK_MODEL_CACHE.containsKey(compound)) {
            try {
                RPGFramework.getLogger().info("Looking up model for " + compound);
                ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
                IBakedModel model = mesher.getItemModel(itemStack);
                ModelManager modelManager = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_175617_aL", "modelManager");
                IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = ReflectionHelper.getPrivateValue(ModelManager.class, modelManager, "field_174958_a", "modelRegistry");
                ModelResourceLocation[] keys = modelRegistry.getKeys().toArray(new ModelResourceLocation[modelRegistry.getKeys().size()]);
                for (int i = 0; i < keys.length; i++) {
                    if (modelRegistry.getObject(keys[i]) == model) {
                        ITEM_STACK_MODEL_CACHE.put(compound, keys[i]);
                        return keys[i];
                    }
                }
                ITEM_STACK_MODEL_CACHE.put(compound, getMissingModel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ITEM_STACK_MODEL_CACHE.getOrDefault(compound, getMissingModel());
    }

    public static void clearItemStackModelCache() {
        ITEM_STACK_MODEL_CACHE.clear();
    }

    public static boolean isMissingModel(ModelResourceLocation modelResourceLocation) {
        return modelResourceLocation == getMissingModel();
    }

    public static ModelResourceLocation getMissingModel() {
        return ModelBakery.MODEL_MISSING;
    }
}
