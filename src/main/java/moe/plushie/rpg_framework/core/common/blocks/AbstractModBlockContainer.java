package moe.plushie.rpg_framework.core.common.blocks;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.client.model.ICustomModel;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.items.block.ModItemBlock;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractModBlockContainer extends BlockContainer implements ICustomItemBlock, ICustomModel {

    private int sortPriority = 100;

    public AbstractModBlockContainer(String name) {
        super(Material.IRON);
        setCreativeTab(RPGFramework.getCreativeTabRPGEconomy());
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    public AbstractModBlockContainer(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(RPGFramework.getCreativeTabRPGEconomy());
        }
        setHardness(3.0F);
        setSoundType(soundType);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    protected static boolean getBitBool(int value, int index) {
        return getBit(value, index) == 1;
    }

    protected static int getBit(int value, int index) {
        return (value >> index) & 1;
    }

    protected static int setBit(int value, int index, boolean on) {
        if (on) {
            return value | (1 << index);
        } else {
            return value & ~(1 << index);
        }
    }

    @Override
    public Block setTranslationKey(String name) {
        super.setTranslationKey(name);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "tile." + name));
        return this;
    }

//    @Override
//    public String getTranslationKey() {
//        String name = super.getTranslationKey().substring(super.getTranslationKey().indexOf(".") + 1);
//        return "tile." + LibModInfo.ID + ":" + name;
//    }

    public AbstractModBlockContainer setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ModItemBlock(this).setRegistryName(getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"));
    }

    protected void openGui(EntityPlayer playerIn, int guiId, World worldIn, BlockPos pos, IBlockState state, EnumFacing facing) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, RPGFramework.getInstance(), guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
