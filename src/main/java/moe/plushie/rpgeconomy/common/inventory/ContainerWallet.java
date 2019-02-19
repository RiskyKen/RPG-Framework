package moe.plushie.rpgeconomy.common.inventory;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerWallet extends ModContainer {

    public ContainerWallet(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
        addPlayerSlots(8, 86);
    }
}
