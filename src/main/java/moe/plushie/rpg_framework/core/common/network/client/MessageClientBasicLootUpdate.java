package moe.plushie.rpg_framework.core.common.network.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerBasicLootBag;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientBasicLootUpdate implements IMessage {

    private int slotIndex;
    private int weight;

    public MessageClientBasicLootUpdate() {
    }

    public MessageClientBasicLootUpdate(int slotIndex, int weight) {
        this.slotIndex = slotIndex;
        this.weight = weight;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotIndex);
        buf.writeInt(weight);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotIndex = buf.readInt();
        weight = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageClientBasicLootUpdate, IMessage> {

        @Override
        public IMessage onMessage(MessageClientBasicLootUpdate message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                
                @Override
                public void run() {
                    updateContainer(ctx.getServerHandler().player, message.slotIndex, message.weight);

                }
            });
            return null;
        }

        private void updateContainer(EntityPlayerMP player, int slotIndex, int weight) {
            if (player.openContainer != null && player.openContainer instanceof ContainerBasicLootBag) {
                ((ContainerBasicLootBag)player.openContainer).clientUpdatedSlot(player, slotIndex, weight);
            }
        }
    }
}
