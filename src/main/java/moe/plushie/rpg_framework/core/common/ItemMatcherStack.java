package moe.plushie.rpg_framework.core.common;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import net.minecraft.item.ItemStack;

public class ItemMatcherStack implements IItemMatcher {

    private final ItemStack itemStack;
    private final boolean matchMeta;
    private final boolean matchNBT;

    public ItemMatcherStack(ItemStack itemStack, boolean matchMeta, boolean matchNBT) {
        this.itemStack = itemStack;
        this.matchMeta = matchMeta;
        this.matchNBT = matchNBT;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (this.itemStack.getItem() != itemStack.getItem()) {
            return false;
        }

        if (matchMeta) {
            if (this.itemStack.getItemDamage() != itemStack.getItemDamage()) {
                return false;
            }
        }

        if (matchNBT) {
            if (this.itemStack.hasTagCompound() != itemStack.hasTagCompound()) {
                return false;
            }
            if (this.itemStack.hasTagCompound() & itemStack.hasTagCompound()) {
                if (!this.itemStack.getTagCompound().equals(itemStack.getTagCompound())) {
                    return false;
                }
                if (!this.itemStack.areCapsCompatible(itemStack)) {
                    return false;
                }
            }
        }

        return true;
    }
    
    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isMatchMeta() {
        return matchMeta;
    }

    public boolean isMatchNBT() {
        return matchNBT;
    }
}
