package moe.plushie.rpgeconomy.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.bank.common.serialize.BankSerializer;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncBanks implements IMessage, IMessageHandler<MessageServerSyncBanks, IMessage> {

    private IBank[] banks;
    
    public MessageServerSyncBanks() {
    }
    
    public MessageServerSyncBanks(IBank[] banks) {
        this.banks = banks;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(banks.length);
        for (int i = 0; i < banks.length; i++) {
            JsonElement jsonCurrency = BankSerializer.serializeJson(banks[i], true);
            ByteBufUtils.writeUTF8String(buf, banks[i].getIdentifier());
            ByteBufUtils.writeUTF8String(buf, jsonCurrency.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        banks = new IBank[size];
        for (int i = 0; i < size; i++) {
            String identifier = ByteBufUtils.readUTF8String(buf);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement jsonCurrency = SerializeHelper.stringToJson(jsonString);
            if (jsonCurrency != null) {
                banks[i] = BankSerializer.deserializeJson(jsonCurrency, identifier);
            }
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncBanks message, MessageContext ctx) {
        RpgEconomy.getProxy().getBankManager().gotBanksFromServer(message.banks);
        return null;
    }
}
