package moe.plushie.rpgeconomy.core.common.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiButton implements IMessage, IMessageHandler<MessageClientGuiButton, IMessage> {

    private int buttonID;

    public MessageClientGuiButton setButtonID(int buttonID) {
        this.buttonID = buttonID;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buttonID);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonID = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageClientGuiButton message, MessageContext ctx) {
        Container container = ctx.getServerHandler().player.openContainer;
        if (container != null && container instanceof IButtonPress) {
            ((IButtonPress) container).buttonPress(message.buttonID);
        }
        return null;
    }

    public interface IButtonPress {

        public void buttonPress(int buttonID);
    }
}
