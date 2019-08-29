package moe.plushie.rpg_framework.core.common.network.server;

import java.util.ArrayList;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.serialize.MailMessageSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerMailList implements IMessage {

    private ArrayList<MailMessage> mailMessages;

    public MessageServerMailList() {
    }

    public MessageServerMailList(ArrayList<MailMessage> mailMessages) {
        this.mailMessages = mailMessages;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mailMessages.size());
        for (int i = 0; i < mailMessages.size(); i++) {
            MailMessage mailMessage = mailMessages.get(i);
            JsonElement mailJson = MailMessageSerializer.serializeJson(mailMessage, true);
            ByteBufUtils.writeUTF8String(buf, mailJson.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mailMessages = new ArrayList<MailMessage>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            JsonElement mailJson = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
            MailMessage mailMessage = MailMessageSerializer.deserializeJson(mailJson);
            mailMessages.add(mailMessage);
        }
    }

    public static class Handler implements IMessageHandler<MessageServerMailList, IMessage> {

        @Override
        public IMessage onMessage(MessageServerMailList message, MessageContext ctx) {
            setList(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void setList(MessageServerMailList message) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    GuiScreen guiScreen = mc.currentScreen;
                    if (guiScreen != null && guiScreen instanceof GuiMailBox) {
                        ((GuiMailBox) guiScreen).gotListFromServer(message.mailMessages);
                    }
                }
            });
        }
    }
}
