package moe.plushie.rpg_framework.mail.common;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailUnreadCount;
import moe.plushie.rpg_framework.core.database.TableMail;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MailNotificationManager {

    public MailNotificationManager() {
    }

    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            syncToClient((EntityPlayerMP) event.player, true, false);
        }
    }

    public void syncToClient(EntityPlayerMP entityPlayer, boolean login, boolean update) {
        RPGFramework.getLogger().info("Sending mail notifications to player " + entityPlayer.getName() + ".");
        for (IMailSystem mailSystem : RPGFramework.getProxy().getMailSystemManager().getMailSystems()) {
            PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(mailSystem, entityPlayer, login, update), entityPlayer);
        }
    }

    private IMessage getSyncMessage(IMailSystem mailSystem, EntityPlayerMP entityPlayer, boolean login, boolean update) {
        int messageCount = TableMail.getUnreadMessagesCount(entityPlayer, mailSystem);
        return new MessageServerMailUnreadCount(mailSystem, messageCount, login, update);
    }
}
