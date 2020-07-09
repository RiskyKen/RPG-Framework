package moe.plushie.rpg_framework.mail.common.items.block;

import java.util.List;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.items.block.ModItemBlock;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.MailSystemManager;
import moe.plushie.rpg_framework.mail.common.blocks.BlockMailBox.MailboxTexture;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockMailBox extends ModItemBlock {

    private static final String TAG_MAIL_SYSTEM = "mailSystem";

    public ItemBlockMailBox(Block block) {
        super(block);
        hasSubtypes = true;
        makeSubNames = false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            MailSystemManager mailSystemManager = RPGFramework.getProxy().getMailSystemManager();
            for (MailSystem mailSystem : mailSystemManager.getMailSystems()) {
                for (int i = 0; i < MailboxTexture.values().length; i++) {
                    ItemStack stack = new ItemStack(this, 1, i);
                    if (!stack.isEmpty()) {
                        setMailSystemOnStack(stack, mailSystem);
                        items.add(stack);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        MailboxTexture texture = MailboxTexture.BLUE;
        if (stack.getMetadata() >= 0 & stack.getMetadata() < MailboxTexture.values().length) {
            texture = MailboxTexture.values()[stack.getMetadata()];
        }
        tooltip.add("Style: " + texture.toString().toLowerCase());
        MailSystem mailSystem = getMailSystemFromStack(stack);
        if (mailSystem != null) {
            tooltip.add("Mail System: " + mailSystem.getName());
        }
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

    public ItemStack getStackFromMailSystem(MailSystem mailSystem) {
        ItemStack itemStack = new ItemStack(block);
        setMailSystemOnStack(itemStack, mailSystem);
        return itemStack;
    }

    public static void setMailSystemOnStack(ItemStack itemStack, MailSystem mailSystem) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.getTagCompound().setString(TAG_MAIL_SYSTEM, (String) mailSystem.getIdentifier().getValue());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean flag = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!world.isRemote) {
            MailSystem mailSystem = getMailSystemFromStack(stack);
            MailboxTexture texture = MailboxTexture.BLUE;
            if (stack.getMetadata() >= 0 & stack.getMetadata() < MailboxTexture.values().length) {
                texture = MailboxTexture.values()[stack.getMetadata()];
            }
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && tileEntity instanceof TileEntityMailBox) {
                ((TileEntityMailBox) tileEntity).setMailSystem(mailSystem.getIdentifier());
                ((TileEntityMailBox) tileEntity).setMailboxTexture(texture);
            }
        }
        return flag;
    }
}
