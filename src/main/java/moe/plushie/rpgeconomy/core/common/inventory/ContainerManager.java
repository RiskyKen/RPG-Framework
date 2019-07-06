package moe.plushie.rpgeconomy.core.common.inventory;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerManager extends ModContainer {

    public ContainerManager(EntityPlayer player) {
        super(player.inventory);
    }
}
