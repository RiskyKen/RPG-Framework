package moe.plushie.rpg_framework.core.common;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum CostType {

    FREE, CURRENCY, ITEMS, ORE_DICTIONARY, ITEM_VALUE;

    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format("cost_type." + LibModInfo.ID.toLowerCase() + ":" + toString().toLowerCase());
    }
}
