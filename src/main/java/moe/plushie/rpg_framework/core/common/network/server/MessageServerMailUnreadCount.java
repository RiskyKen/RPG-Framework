package moe.plushie.rpg_framework.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
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
    private boolean notification;

    public MessageServerMailUnreadCount() {
    }

    public MessageServerMailUnreadCount(IMailSystem mailSystem, int unreadCount, boolean notification) {
        this.mailSystem = mailSystem;
        this.unreadCount = unreadCount;
        this.notification = notification;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(mailSystem.getIdentifier()).toString());
        buf.writeInt(unreadCount);
        buf.writeBoolean(notification);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        JsonElement jsonElement = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
        IIdentifier identifier = IdentifierSerialize.deserializeJson(jsonElement);
        mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(identifier);
        unreadCount = buf.readInt();
        notification = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<MessageServerMailUnreadCount, IMessage> {

        @Override
        public IMessage onMessage(MessageServerMailUnreadCount message, MessageContext ctx) {
            setMessageCount(message.mailSystem, message.unreadCount, message.notification);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void setMessageCount(IMailSystem mailSystem, int unreadCount, boolean notification) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    MailCounter.setUnreadMailCount(mailSystem, unreadCount, notification);
                    if (notification & unreadCount > 0) {
                        TextComponentTranslation component = new TextComponentTranslation("chat." + LibModInfo.ID + ":unreadMessageCount", unreadCount);
                        mc.player.sendMessage(component);
                    }
                }
            });
        }
    }
}
