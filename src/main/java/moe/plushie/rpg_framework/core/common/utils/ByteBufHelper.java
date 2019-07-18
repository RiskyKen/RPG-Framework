package moe.plushie.rpg_framework.core.common.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public final class ByteBufHelper {

    private ByteBufHelper() {
    }

    public static void writeIdentifier(ByteBuf buf, IIdentifier identifier) {
        ByteBufUtils.writeUTF8String(buf, IdentifierSerialize.serializeJson(identifier).toString());
    }

    public static IIdentifier readIdentifier(ByteBuf buf) {
        return IdentifierSerialize.deserializeJson(SerializeHelper.stringToJson(ByteBufUtils.readUTF8String(buf)));
    }

    public static void writeString(ByteBuf buf, String string) {
        byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(utf8Bytes.length);
        
        try (ByteBufOutputStream outputStream = new ByteBufOutputStream(buf); GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            outputStream.write(utf8Bytes);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt();
        byte[] utf8Bytes = new byte[length];
        
        try(ByteBufInputStream inputStream = new ByteBufInputStream(buf); GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            inputStream.read(utf8Bytes, 0, length);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }
}
