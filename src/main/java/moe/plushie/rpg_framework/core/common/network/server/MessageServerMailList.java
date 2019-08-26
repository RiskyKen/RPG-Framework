package moe.plushie.rpg_framework.core.common.network.server;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import moe.plushie.rpg_framework.mail.common.MailListItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerMailList implements IMessage {

    private ArrayList<MailListItem> listItems;

    public MessageServerMailList() {
    }

    public MessageServerMailList(ArrayList<MailListItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(listItems.size());
        for (int i = 0; i < listItems.size(); i++) {
            MailListItem listItem = listItems.get(i);
            buf.writeInt(listItem.getId());
            ByteBufUtils.writeUTF8String(buf, listItem.getSubject());
            buf.writeBoolean(listItem.hasItems());
            buf.writeBoolean(listItem.isRead());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        listItems = new ArrayList<MailListItem>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            String subject = ByteBufUtils.readUTF8String(buf);
            boolean hasItems = buf.readBoolean();
            boolean read = buf.readBoolean();
            listItems.add(new MailListItem(id, subject, hasItems, read));
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
                        ((GuiMailBox) guiScreen).gotListFromServer(message.listItems);
                    }
                }
            });
        }
    }
}
