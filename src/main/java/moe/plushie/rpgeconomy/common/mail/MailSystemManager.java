package moe.plushie.rpgeconomy.common.mail;

import java.io.File;

import net.minecraft.entity.player.EntityPlayerMP;

public class MailSystemManager {

    private final MailSystem mailSystemMain;

    public MailSystemManager(File modDirectory) {
        mailSystemMain = new MailSystem("main");
    }

    public void reload(boolean syncWithClients) {

    }

    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        mailSystemMain.onClientSendMailMessage(entityPlayer, mailMessage);
    }
}
