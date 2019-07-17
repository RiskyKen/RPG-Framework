package moe.plushie.rpg_framework.core.common.utils;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public final class ByteBufHelper {

    private ByteBufHelper() {
    }
    
    public static IIdentifier readIdentifier(ByteBuf buf) {
        return IdentifierSerialize.deserializeJson(SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf)));
    }
    
    public static void writeIdentifier(ByteBuf buf, IIdentifier identifier) {
        ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(identifier).toString());
    }
}
