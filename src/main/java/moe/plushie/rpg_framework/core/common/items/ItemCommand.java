package moe.plushie.rpg_framework.core.common.items;

import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.lib.LibItemNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCommand extends AbstractModItem {

    public ItemCommand() {
        super(LibItemNames.COMMAND);
        setMaxStackSize(64);
    }

    public boolean haveRenderTarget(ItemStack itemStack) {
        if (itemStack.getCount() == 1) {
            return false;
        }
        return true;
    }

    public ItemStack getRenderTarget(ItemStack itemStack) {
        ItemStack returnStack = new ItemStack(this);

        if (itemStack.getCount() == 2) {
            returnStack = new ItemStack(Blocks.DIRT);
        }
        if (itemStack.getCount() == 3) {
            returnStack = new ItemStack(Items.APPLE);
        }
        if (itemStack.getCount() == 4) {
            returnStack = new ItemStack(ModBlocks.MAIL_BOX);
        }
        if (itemStack.getCount() == 5) {
            returnStack = new ItemStack(ModBlocks.MAIL_BOX, 1, 6);
        }
        if (itemStack.getCount() == 6) {
            returnStack = new ItemStack(ModBlocks.SHOP);
        }
        if (itemStack.getCount() == 7) {
            returnStack = new ItemStack(ModItems.BASIC_LOOT_BAG);
        }

        return returnStack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {

            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                ModelResourceLocation mrl = null;
                if (haveRenderTarget(stack)) {
                    ItemStack renderStack = getRenderTarget(stack);
                    try {
                        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
                        IBakedModel model = mesher.getItemModel(renderStack);

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
                }
                if (mrl == null) {
                    mrl = modelResourceLocation;
                }
                return mrl;
            }
        });
        ModelBakery.registerItemVariants(this, modelResourceLocation);
    }

    private int getIndex(Item item, int meta) {
        return Item.getIdFromItem(item) << 16 | meta;
    }
}
