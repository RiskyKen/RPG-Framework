package moe.plushie.rpg_framework.api.core;

import net.minecraft.item.ItemStack;

public interface IItemMatcher {

    public boolean matches(ItemStack itemStack);

    public ItemStack getItemStack();

    public boolean isMatchMeta();

    public boolean isMatchCount();

    public boolean isMatchNBT();
}
