package moe.plushie.rpg_framework.bank.common.items.block;

import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.bank.common.BankManager;
import moe.plushie.rpg_framework.bank.tileentities.TileEntityBank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.items.block.ModItemBlock;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemBlockBank extends ModItemBlock {

    private static final String TAG_BANK = "bank";

    public ItemBlockBank(Block block) {
        super(block);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            BankManager bankManager = RPGFramework.getProxy().getBankManager();
            for (IBank bank : bankManager.getBanks()) {
                ItemStack itemStack = getStackFromBank(bank);
                if (!itemStack.isEmpty()) {
                    items.add(itemStack);
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        IBank bank = getBankFromStack(stack);
        if (bank != null) {
            return super.getItemStackDisplayName(stack) + " (" + bank.getName() + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    public static IBank getBankFromStack(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey(TAG_BANK, NBT.TAG_STRING)) {
                BankManager bankManager = RPGFramework.getProxy().getBankManager();
                JsonElement jsonElement = SerializeHelper.stringToJson(itemStack.getTagCompound().getString(TAG_BANK));
                return bankManager.getBank(IdentifierSerialize.deserializeJson(jsonElement));
            }
        }
        return null;
    }

    public static ItemStack getStackFromBank(IBank bank) {
        ItemStack itemStack = new ItemStack(ModBlocks.BANK);
        setBankOnStack(itemStack, bank);
        return itemStack;
    }

    public static void setBankOnStack(ItemStack itemStack, IBank bank) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.getTagCompound().setString(TAG_BANK, IdentifierSerialize.serializeJson(bank.getIdentifier()).toString());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean flag = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        IBank bank = getBankFromStack(stack);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityBank) {
            ((TileEntityBank) tileEntity).setBank(bank);
        }
        return flag;
    }
}
