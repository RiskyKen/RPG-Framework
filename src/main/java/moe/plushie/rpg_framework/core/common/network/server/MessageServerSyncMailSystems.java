package moe.plushie.rpg_framework.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.serialize.MailSystemSerializer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncMailSystems implements IMessage, IMessageHandler<MessageServerSyncMailSystems, IMessage> {

    private MailSystem[] mailSystems;

    public MessageServerSyncMailSystems() {
    }

    public MessageServerSyncMailSystems(MailSystem[] mailSystems) {
        this.mailSystems = mailSystems;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mailSystems.length);
        for (int i = 0; i < mailSystems.length; i++) {
            JsonElement jsonCurrency = MailSystemSerializer.serializeJson(mailSystems[i]);
            ByteBufUtils.writeUTF8String(buf, mailSystems[i].getIdentifier());
            ByteBufUtils.writeUTF8String(buf, jsonCurrency.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        mailSystems = new MailSystem[size];
        for (int i = 0; i < size; i++) {
            String identifier = ByteBufUtils.readUTF8String(buf);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement jsonCurrency = SerializeHelper.stringToJson(jsonString);
            if (jsonCurrency != null) {
                mailSystems[i] = MailSystemSerializer.deserializeJson(jsonCurrency, identifier);
            }
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncMailSystems message, MessageContext ctx) {
        RpgEconomy.getProxy().getMailSystemManager().gotMailSystemsFromServer(message.mailSystems);
        return null;
    }
}
