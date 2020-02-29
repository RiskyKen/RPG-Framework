package moe.plushie.rpg_framework.core.common.items;

import java.util.List;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.model.ICustomModel;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractModItem extends Item implements ICustomModel {

    private int sortPriority = 0;

    public AbstractModItem(String name) {
        this(name, true);
    }

    public AbstractModItem(String name, boolean addCreativeTab) {
        if (addCreativeTab) {
            setCreativeTab(RPGFramework.getCreativeTabRPGEconomy());
        }
        setTranslationKey(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
        setNoRepair();
        ModItems.ITEM_LIST.add(this);
    }

    @Override
    public Item setTranslationKey(String unlocalizedName) {
        super.setTranslationKey(unlocalizedName);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "item." + unlocalizedName));
        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String unlocalized;
        String localized;

        unlocalized = stack.getTranslationKey() + ".tooltip";
        localized = I18n.format(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("\r\n")) {
                String[] split = localized.split("\r\n");
                for (int i = 0; i < split.length; i++) {
                    tooltip.add(split[i]);
                }
            } else {
                tooltip.add(localized);
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getModdedUnlocalizedName(super.getTranslationKey(stack), stack);
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name + ".0";
        } else {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
        }
    }

    protected String getModdedUnlocalizedName(String unlocalizedName, ItemStack stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name + "." + stack.getItemDamage();
        } else {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
        }
    }

    public AbstractModItem setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory"));
    }
}
