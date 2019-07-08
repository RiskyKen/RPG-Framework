package moe.plushie.rpgeconomy.loot.common.inventory;

import moe.plushie.rpgeconomy.core.common.inventory.ModContainer;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerLootEditor extends ModContainer {

    public ContainerLootEditor(EntityPlayer player) {
        super(player.inventory);
    }
}
