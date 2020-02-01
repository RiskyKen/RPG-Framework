package moe.plushie.rpg_framework.mail.common.blocks;

import moe.plushie.rpg_framework.core.common.blocks.AbstractModBlockContainer;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.items.block.ItemBlockMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockMailBox extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<MailboxTexture> STATE_STYLE = PropertyEnum.<MailboxTexture>create("style", MailboxTexture.class);

    public BlockMailBox() {
        super(LibBlockNames.MAIL_BOX);
        setDefaultState(this.blockState.getBaseState().withProperty(STATE_FACING, EnumFacing.NORTH).withProperty(STATE_STYLE, MailboxTexture.BLUE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE_FACING, STATE_STYLE });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean northSouthBit = getBitBool(meta, 0);
        boolean posNegBit = getBitBool(meta, 1);
        EnumFacing facing = EnumFacing.EAST;
        if (northSouthBit) {
            if (posNegBit) {
                facing = EnumFacing.SOUTH;
            } else {
                facing = EnumFacing.NORTH;
            }
        } else {
            if (posNegBit) {
                facing = EnumFacing.EAST;
            } else {
                facing = EnumFacing.WEST;
            }
        }
        return this.getDefaultState().withProperty(STATE_FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(STATE_FACING);
        int meta = 0;
        if (facing == EnumFacing.NORTH | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 0, true);
        }
        if (facing == EnumFacing.EAST | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 1, true);
        }
        return meta;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < MailboxTexture.values().length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        ItemStack stack = placer.getHeldItem(hand);
        MailboxTexture texture = MailboxTexture.BLUE;
        if (stack.getMetadata() >= 0 & stack.getMetadata() < MailboxTexture.values().length) {
            texture = MailboxTexture.values()[stack.getMetadata()];
        }
        EnumFacing enumfacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(STATE_FACING, enumfacing).withProperty(STATE_STYLE, texture);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMailBox) {
            MailboxTexture texture = ((TileEntityMailBox) te).getMailboxTexture();
            if (texture != null) {
                return state.withProperty(STATE_STYLE, texture);
            }
        }
        return state;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        openGui(playerIn, EnumGuiId.MAIL_BOX.ordinal(), worldIn, pos, state, facing);
        return true;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockMailBox(this).setRegistryName(getRegistryName()));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMailBox();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack itemStack = super.getPickBlock(state, target, world, pos, player);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityMailBox) {
            MailSystem mailSystem = ((TileEntityMailBox) tileEntity).getMailSystem();
            MailboxTexture texture = ((TileEntityMailBox) tileEntity).getMailboxTexture();
            if (mailSystem != null) {
                ItemBlockMailBox.setMailSystemOnStack(itemStack, mailSystem);
            }
            if (texture != null) {
                itemStack.setItemDamage(texture.ordinal());
            }
        }
        return itemStack;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (worldIn.isRemote) {
            return;
        }
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        Item item = this.getItemDropped(state, worldIn.rand, i);
        if (item == Items.AIR) {
            return;
        }
        ItemStack itemStack = new ItemStack(item, this.quantityDropped(worldIn.rand));
        if (te != null && te instanceof TileEntityMailBox) {
            MailSystem mailSystem = ((TileEntityMailBox) te).getMailSystem();
            if (mailSystem != null) {
                ItemBlockMailBox.setMailSystemOnStack(itemStack, mailSystem);
            }
        }
        spawnAsEntity(worldIn, pos, itemStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"));
        for (int i = 0; i < MailboxTexture.values().length; i++) {
            ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), MailboxTexture.values()[i].toString());
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, mrl);
            //ModelLoader.registerItemVariants(Item.getItemFromBlock(this), mrl);
        }
        
    }

    public static enum MailboxTexture implements IStringSerializable {
        BLUE, GRAYSCALE, LOG_1, LOG_2, LOG_3, LOG_4, LOG_5;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
