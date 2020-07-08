package moe.plushie.rpg_framework.core.common;

import java.util.LinkedHashMap;
import java.util.Set;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemMatcherStack implements IItemMatcher {

    private LinkedHashMap<String, NBTBase> tagsRequired = new LinkedHashMap<>();
    private LinkedHashMap<String, NBTBase> tagsDisallowed = new LinkedHashMap<>();

    private final ItemStack itemStack;
    private final boolean matchMeta;
    private final boolean matchCount;
    private final boolean matchNBT;

    public ItemMatcherStack(ItemStack itemStack, boolean matchMeta, boolean matchNBT, boolean matchCount) {
        this.itemStack = itemStack;
        this.matchMeta = matchMeta;
        this.matchCount = matchCount;
        this.matchNBT = matchNBT;
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compound = itemStack.getTagCompound();
            Set<String> keys = compound.getKeySet();
            for (String key : keys) {
                NBTBase tag = compound.getTag(key);
                tagsRequired.put(key, tag);
            }
        }
    }

    public ItemMatcherStack(ItemStack itemStack, boolean matchMeta, boolean matchNBT) {
        this(itemStack, matchMeta, matchNBT, false);
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (!isItemMatch(itemStack)) {
            return false;
        }

        if (!isMetaMatch(itemStack)) {
            return false;
        }

        if (!isCountMatch(itemStack)) {
            return false;
        }

        if (!isNBTMatch(itemStack)) {
            return false;
        }

        return true;
    }

    private boolean isItemMatch(ItemStack itemStack) {
        return this.itemStack.getItem() == itemStack.getItem();
    }

    private boolean isMetaMatch(ItemStack itemStack) {
        if (!matchMeta) {
            return true;
        }
        return this.itemStack.getItemDamage() == itemStack.getItemDamage();
    }

    private boolean isCountMatch(ItemStack itemStack) {
        return this.itemStack.getCount() == itemStack.getCount();
    }

    private boolean isNBTMatch(ItemStack itemStack) {
        if (!matchNBT) {
            return true;
        }
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
        return true;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean isMatchMeta() {
        return matchMeta;
    }
    
    @Override
    public boolean isMatchCount() {
        return matchCount;
    }
    
    @Override
    public boolean isMatchNBT() {
        return matchNBT;
    }
}
