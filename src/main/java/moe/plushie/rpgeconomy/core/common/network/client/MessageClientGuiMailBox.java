package moe.plushie.rpgeconomy.core.common.network.client;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.mail.common.MailMessage;
import moe.plushie.rpgeconomy.mail.common.serialize.MailMessageSerializer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMailBox implements IMessage, IMessageHandler<MessageClientGuiMailBox, IMessage> {
    
    private MailMessage mailMessage;
    
    public MessageClientGuiMailBox() {}
    
    public MessageClientGuiMailBox(MailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        JsonElement jsonElement = MailMessageSerializer.serialize(mailMessage);
        RpgEconomy.getLogger().info("Sending json to server. " + jsonElement.toString());
        ByteBufUtils.writeUTF8String(buf, jsonElement.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String mailJson = ByteBufUtils.readUTF8String(buf);
        RpgEconomy.getLogger().info("Got json from client. " + mailJson);
        mailMessage = MailMessageSerializer.deserialize(SerializeHelper.stringToJson(mailJson));
    }

    @Override
    public IMessage onMessage(MessageClientGuiMailBox message, MessageContext ctx) {
        RpgEconomy.getProxy().getMailSystemManager().onClientSendMailMessage(ctx.getServerHandler().player, message.mailMessage);
        return null;
    }
}
