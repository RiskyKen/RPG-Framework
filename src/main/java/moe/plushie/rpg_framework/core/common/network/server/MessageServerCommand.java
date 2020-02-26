package moe.plushie.rpg_framework.core.common.network.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.lwjgl.Sys;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerCommand implements IMessage {

    private ServerCommandType commandType;

    public MessageServerCommand() {
    }

    public MessageServerCommand(ServerCommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(commandType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        commandType = ServerCommandType.values()[buf.readByte()];
    }

    public static class Handler implements IMessageHandler<MessageServerCommand, IMessage> {

        @Override
        public IMessage onMessage(MessageServerCommand message, MessageContext ctx) {
            switch (message.commandType) {
            case OPEN_PACK_FOLDER:
                openModFolder();
                break;
            }
            return null;
        }

        private void openModFolder() {
            openFolder(RPGFramework.getProxy().getModDirectory());
        }

        @SideOnly(Side.CLIENT)
        private void openFolder(File folder) {
            String packPath = folder.getAbsolutePath();

            if (Util.getOSType() == Util.EnumOS.OSX) {
                try {
                    Runtime.getRuntime().exec(new String[] { "/usr/bin/open", packPath });
                    return;
                } catch (IOException ioexception1) {
                    RPGFramework.getLogger().error("Couldn\'t open file: " + ioexception1);
                }
            } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] { packPath });
                try {
                    Runtime.getRuntime().exec(s1);
                    return;
                } catch (IOException ioexception) {
                    RPGFramework.getLogger().error("Couldn\'t open file: " + ioexception);
                }
            }

            boolean openedFailed = false;

            try {
                Class oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
                oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { folder.toURI() });
            } catch (Throwable throwable) {
                RPGFramework.getLogger().error("Couldn\'t open link: " + throwable);
                openedFailed = true;
            }

            if (openedFailed) {
                RPGFramework.getLogger().error("Opening via system class!");
                Sys.openURL("file://" + packPath);
            }
        }
    }

    public static enum ServerCommandType {
        OPEN_PACK_FOLDER;
    }
}
