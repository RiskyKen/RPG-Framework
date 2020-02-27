package moe.plushie.rpg_framework.stats.common.inventory;

import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerStats extends ModContainer {

    public ContainerStats(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
    }
}
