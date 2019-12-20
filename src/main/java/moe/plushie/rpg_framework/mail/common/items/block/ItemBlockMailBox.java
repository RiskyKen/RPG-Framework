package moe.plushie.rpg_framework.mail.common.items.block;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.items.block.ModItemBlock;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.MailSystemManager;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
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

public class ItemBlockMailBox extends ModItemBlock {

    private static final String TAG_MAIL_SYSTEM = "mailSystem";

    public ItemBlockMailBox(Block block) {
        super(block);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            MailSystemManager mailSystemManager = RPGFramework.getProxy().getMailSystemManager();
            for (MailSystem mailSystem : mailSystemManager.getMailSystems()) {
                ItemStack itemStack = getStackFromMailSystem(mailSystem);
                if (!itemStack.isEmpty()) {
                    items.add(itemStack);
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        MailSystem mailSystem = getMailSystemFromStack(stack);
        if (mailSystem != null) {
            return super.getItemStackDisplayName(stack) + " (" + mailSystem.getName() + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    public static MailSystem getMailSystemFromStack(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
                MailSystemManager mailSystemManager = RPGFramework.getProxy().getMailSystemManager();
                return mailSystemManager.getMailSystem(new IdentifierString(itemStack.getTagCompound().getString(TAG_MAIL_SYSTEM)));
            }
        }
        return null;
    }

    public static ItemStack getStackFromMailSystem(MailSystem mailSystem) {
        ItemStack itemStack = new ItemStack(ModBlocks.MAIL_BOX);
        setMailSystemOnStack(itemStack, mailSystem);
        return itemStack;
    }

    public static void setMailSystemOnStack(ItemStack itemStack, MailSystem mailSystem) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.getTagCompound().setString(TAG_MAIL_SYSTEM, mailSystem.getIdentifier().toString());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean flag = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        MailSystem mailSystem = getMailSystemFromStack(stack);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityMailBox) {
            ((TileEntityMailBox) tileEntity).setMailSystem(mailSystem);
        }
        return flag;
    }
}
