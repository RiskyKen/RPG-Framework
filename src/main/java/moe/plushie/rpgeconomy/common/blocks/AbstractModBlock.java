package moe.plushie.rpgeconomy.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import moe.plushie.rpgeconomy.RPGEconomy;
import moe.plushie.rpgeconomy.common.items.block.ModItemBlock;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class AbstractModBlock extends Block {
    
    public AbstractModBlock(String name) {
        super(Material.iron);
        setCreativeTab(RPGEconomy.getCreativetabrpgeconomy());
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
        setBlockName(name);
    }
    
    public AbstractModBlock(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(RPGEconomy.getCreativetabrpgeconomy());
        }
        setHardness(3.0F);
        setStepSound(soundType);
        setBlockName(name);
    }
    
    @Override
    public Block setBlockName(String name) {
        registerBlock(name);
        return super.setBlockName(name);
    }

    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
    
    protected void registerBlock(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
    }
}
