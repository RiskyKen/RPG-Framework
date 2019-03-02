package moe.plushie.rpgeconomy.mail.common.items.block;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.items.block.ModItemBlock;
import moe.plushie.rpgeconomy.mail.common.MailSystem;
import moe.plushie.rpgeconomy.mail.common.MailSystemManager;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemBlockMailBox extends ModItemBlock {

    private static final String TAG_MAIL_SYSTEM = "mailSystem";

    public ItemBlockMailBox(Block block) {
        super(block);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            MailSystemManager mailSystemManager = RpgEconomy.getProxy().getMailSystemManager();
            for (MailSystem mailSystem : mailSystemManager.getMailSystems()) {
                ItemStack itemStack = getMailBox(mailSystem);
                if (!itemStack.isEmpty()) {
                    items.add(itemStack);
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        MailSystem mailSystem = getMailSystem(stack);
        if (mailSystem != null) {
            return super.getItemStackDisplayName(stack) + " (" + mailSystem.getName() + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    public static MailSystem getMailSystem(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
                MailSystemManager mailSystemManager = RpgEconomy.getProxy().getMailSystemManager();
                return mailSystemManager.getMailSystem(itemStack.getTagCompound().getString(TAG_MAIL_SYSTEM));
            }
        }
        return null;
    }

    public static ItemStack getMailBox(MailSystem mailSystem) {
        ItemStack itemStack = new ItemStack(ModBlocks.MAIL_BOX);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString(TAG_MAIL_SYSTEM, mailSystem.getName());
        itemStack.setTagCompound(compound);
        return itemStack;
    }
}
