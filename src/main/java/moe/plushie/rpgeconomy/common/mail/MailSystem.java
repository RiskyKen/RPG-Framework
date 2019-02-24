package moe.plushie.rpgeconomy.common.mail;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.api.mail.IMailSystem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class MailSystem implements IMailSystem {

    private final String name;

    public MailSystem(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            entityPlayer.entityDropItem(itemStack, entityPlayer.eyeHeight);
        }
        RpgEconomy.getLogger().info("got mail message from player " + mailMessage);
    }
}
