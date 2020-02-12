package moe.plushie.rpg_framework.core.common.network.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.config.ConfigOptionsShared;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncConfig implements IMessage {

    private ConfigOptionsShared options;

    public MessageServerSyncConfig() {
    }

    public MessageServerSyncConfig(ConfigOptionsShared options) {
        this.options = options;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Gson gson = new GsonBuilder().create();
        ByteBufUtils.writeUTF8String(buf, gson.toJsonTree(options).toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Gson gson = new GsonBuilder().create();
        JsonElement jsonElement = SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf));
        options = gson.fromJson(jsonElement, ConfigOptionsShared.class);
    }

    public static class Handler implements IMessageHandler<MessageServerSyncConfig, IMessage> {

        @Override
        public IMessage onMessage(MessageServerSyncConfig message, MessageContext ctx) {
            ConfigHandler.optionsShared = message.options;
            return null;
        }
    }
}
