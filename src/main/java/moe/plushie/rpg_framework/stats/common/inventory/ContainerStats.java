package moe.plushie.rpg_framework.stats.common.inventory;

import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerStats extends ModContainer {

    private int counter;
    
    public ContainerStats(EntityPlayer entityPlayer) {
        super(entityPlayer.inventory);
        sendStatsToClient(true);
    }
    
    private void sendStatsToClient(boolean fullStats) {
        if (invPlayer.player.getEntityWorld().isRemote) {
            return;
        }
        
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        counter++;
        if (counter >= 20) {
            counter = 0;
            sendStatsToClient(false);
        }
    }
}
