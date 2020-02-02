package moe.plushie.rpg_framework.core.common.network.server;

import java.util.UUID;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.init.ModSounds;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.client.MailCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerMailUnreadCount implements IMessage {

    private IMailSystem mailSystem;
    private int unreadCount;
    private boolean login;
    private boolean update;

    public MessageServerMailUnreadCount() {
    }

    public MessageServerMailUnreadCount(IMailSystem mailSystem, int unreadCount, boolean login, boolean update) {
        this.mailSystem = mailSystem;
        this.unreadCount = unreadCount;
        this.login = login;
        this.update = update;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(mailSystem.getIdentifier()).toString());
        buf.writeInt(unreadCount);
        buf.writeBoolean(login);
        buf.writeBoolean(update);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        JsonElement jsonElement = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
        IIdentifier identifier = IdentifierSerialize.deserializeJson(jsonElement);
        mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(identifier);
        unreadCount = buf.readInt();
        login = buf.readBoolean();
        update = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<MessageServerMailUnreadCount, IMessage> {

        private static final GameProfile PROFILE_SKY = new GameProfile(UUID.fromString("2b10d8f1-3273-48a8-9061-cd5e02f45be2"), "skylandersking");
        
        @Override
        public IMessage onMessage(MessageServerMailUnreadCount message, MessageContext ctx) {
            setMessageCount(message.mailSystem, message.unreadCount, message.login, message.update);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void setMessageCount(IMailSystem mailSystem, int unreadCount, boolean login, boolean update) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    MailCounter.setUnreadMailCount(mailSystem, unreadCount);
                    if (unreadCount > 0) {
                        boolean display = false;
                        if (mailSystem.isChatNotificationAtLogin() & login) {
                            display = true;
                        }
                        if (mailSystem.isChatNotificationOnNewMessage() & update) {
                            display = true;
                        }
                        if (display) {
                            TextComponentTranslation component = new TextComponentTranslation("chat." + LibModInfo.ID + ":unreadMessageCount", unreadCount);
                            mc.player.sendMessage(component);
                            if (PlayerUtils.gameProfilesMatch(mc.player.getGameProfile(), PROFILE_SKY)) {
                                mc.player.playSound(ModSounds.BOOP, 1.0F, 1.0F);
                            } else {
                                mc.player.playSound(ModSounds.MAIL_RECEIVED, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            });
        }
    }
}
