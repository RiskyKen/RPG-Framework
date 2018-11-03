package moe.plushie.rpgeconomy.common.mail;

import net.minecraft.entity.player.EntityPlayerMP;

public class MailSystemManager {
    
    private final MailSystem mailSystemMain;
    
    public MailSystemManager() {
        mailSystemMain = new MailSystem("main");
    }
    
    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        mailSystemMain.onClientSendMailMessage(entityPlayer, mailMessage);
    }
}
