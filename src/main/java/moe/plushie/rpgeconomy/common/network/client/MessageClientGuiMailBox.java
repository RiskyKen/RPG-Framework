package moe.plushie.rpgeconomy.common.network.client;

import com.google.gson.JsonElement;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.RPGEconomy;
import moe.plushie.rpgeconomy.common.mail.MailMessage;
import moe.plushie.rpgeconomy.common.mail.serialize.MailMessageSerializer;

public class MessageClientGuiMailBox implements IMessage, IMessageHandler<MessageClientGuiMailBox, IMessage> {
    
    private MailMessage mailMessage;
    
    public MessageClientGuiMailBox() {}
    
    public MessageClientGuiMailBox(MailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        JsonElement jsonElement = MailMessageSerializer.serialize(mailMessage);
        RPGEconomy.getLogger().info("Sending json to server. " + jsonElement.toString());
        ByteBufUtils.writeUTF8String(buf, jsonElement.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String mailJson = ByteBufUtils.readUTF8String(buf);
        RPGEconomy.getLogger().info("Got json from client. " + mailJson);
        mailMessage = MailMessageSerializer.deserialize(mailJson);
    }

    @Override
    public IMessage onMessage(MessageClientGuiMailBox message, MessageContext ctx) {
        RPGEconomy.getProxy().getMailSystemManager().onClientSendMailMessage(ctx.getServerHandler().playerEntity, message.mailMessage);
        return null;
    }
}
