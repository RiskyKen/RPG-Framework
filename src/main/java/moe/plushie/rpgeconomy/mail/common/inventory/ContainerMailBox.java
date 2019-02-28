package moe.plushie.rpgeconomy.mail.common.inventory;

import moe.plushie.rpgeconomy.core.common.inventory.ModContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMailBox extends ModContainer {

    public ContainerMailBox(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
        int playerInvY = 142;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(entityPlayer.inventory, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(entityPlayer.inventory, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
