package moe.plushie.rpg_economy.common.mail;

import moe.plushie.rpg_economy.RPG_Economy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class MailSystem {
    
    private final String name;
    
    public MailSystem(String name) {
        this.name = name;
    }
    
    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            entityPlayer.entityDropItem(itemStack, entityPlayer.eyeHeight);
        }
        RPG_Economy.getLogger().info("got mail message from player " + mailMessage);
    }
}
