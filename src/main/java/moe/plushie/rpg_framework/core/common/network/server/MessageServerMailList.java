package moe.plushie.rpg_framework.core.common.network.server;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerMailList implements IMessage {

    private ArrayList<Integer> ids;
    private ArrayList<String> subjects;
    private ArrayList<Boolean> items;
    private ArrayList<Boolean> read;

    public MessageServerMailList() {
    }

    public MessageServerMailList(ArrayList<Integer> ids, ArrayList<String> subjects, ArrayList<Boolean> items, ArrayList<Boolean> read) {
        this.ids = ids;
        this.subjects = subjects;
        this.items = items;
        this.read = read;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            buf.writeInt(ids.get(i));
            ByteBufUtils.writeUTF8String(buf, subjects.get(i));
            buf.writeBoolean(items.get(i));
            buf.writeBoolean(read.get(i));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ids = new ArrayList<Integer>();
        subjects = new ArrayList<String>();
        items = new ArrayList<Boolean>();
        read = new ArrayList<Boolean>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            ids.add(buf.readInt());
            subjects.add(ByteBufUtils.readUTF8String(buf));
            items.add(buf.readBoolean());
            read.add(buf.readBoolean());
        }
    }

    public static class Handler implements IMessageHandler<MessageServerMailList, IMessage> {

        @Override
        public IMessage onMessage(MessageServerMailList message, MessageContext ctx) {
            setList(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void setList(MessageServerMailList message) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    GuiScreen guiScreen = mc.currentScreen;
                    if (guiScreen != null && guiScreen instanceof GuiMailBox) {
                        ((GuiMailBox)guiScreen).gotListFromServer(message.ids, message.subjects, message.items, message.read);
                    }
                }
            });
        }
    }
}
