package moe.plushie.rpg_framework.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.serialize.CurrencySerializer;
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
            ByteBufHelper.writeIdentifier(buf, currencies[i].getIdentifier());
            JsonElement jsonCurrency = CurrencySerializer.serializeJson(currencies[i], true);
            ByteBufUtils.writeUTF8String(buf, jsonCurrency.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        currencies = new Currency[size];
        for (int i = 0; i < size; i++) {
            IIdentifier identifier = ByteBufHelper.readIdentifier(buf);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement jsonCurrency = SerializeHelper.stringToJson(jsonString);
            if (jsonCurrency != null) {
                currencies[i] = CurrencySerializer.deserializeJson(jsonCurrency, identifier);
            }
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncCurrencies message, MessageContext ctx) {
        RPGFramework.getProxy().getCurrencyManager().gotCurrenciesFromServer(message.currencies);
        return null;
    }
}
