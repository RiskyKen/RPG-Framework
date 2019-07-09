package moe.plushie.rpgeconomy.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.serialize.CurrencySerializer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncCurrencies implements IMessage, IMessageHandler<MessageServerSyncCurrencies, IMessage> {

    private Currency[] currencies;

    public MessageServerSyncCurrencies() {
    }

    public MessageServerSyncCurrencies(Currency[] currencies) {
        this.currencies = currencies;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(currencies.length);
        for (int i = 0; i < currencies.length; i++) {
            JsonElement jsonCurrency = CurrencySerializer.serializeJson(currencies[i], true);
            ByteBufUtils.writeUTF8String(buf, jsonCurrency.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        currencies = new Currency[size];
        for (int i = 0; i < size; i++) {
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement jsonCurrency = SerializeHelper.stringToJson(jsonString);
            if (jsonCurrency != null) {
                currencies[i] = CurrencySerializer.deserializeJson(jsonCurrency);
            }
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncCurrencies message, MessageContext ctx) {
        RpgEconomy.getProxy().getCurrencyManager().gotCurrenciesFromServer(message.currencies);
        return null;
    }
}
