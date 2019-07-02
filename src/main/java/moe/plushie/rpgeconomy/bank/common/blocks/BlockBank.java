package moe.plushie.rpgeconomy.bank.common.blocks;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.bank.common.items.block.ItemBlockBank;
import moe.plushie.rpgeconomy.bank.tileentities.TileEntityBank;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.blocks.AbstractModBlockContainer;
import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.core.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.core.database.Database;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBank extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = BlockHorizontal.FACING;
    
    public BlockBank() {
        super(LibBlockNames.BANK);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE_FACING });
    }

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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing enumfacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(STATE_FACING, enumfacing);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!playerIn.canPlayerEdit(pos, facing, stack)) {
            return false;
        }
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity != null && tileEntity instanceof TileEntityBank) {
                IBank bank = ((TileEntityBank)tileEntity).getBank();
                int index = RpgEconomy.getProxy().getBankManager().getBankIndex(bank);
                int playerId = Database.PLAYERS_TABLE.getPlayerId(playerIn);
                FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), LibGuiIds.BANK, worldIn, index, playerId, 0);
            }
        }
        return true;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockBank(this).setRegistryName(getRegistryName()));
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBank();
    }

}
