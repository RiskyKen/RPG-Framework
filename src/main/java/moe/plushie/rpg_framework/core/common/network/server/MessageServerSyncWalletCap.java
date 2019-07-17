package moe.plushie.rpg_framework.core.common.network.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.network.client.DelayedMessageHandler;
import moe.plushie.rpg_framework.core.common.network.client.DelayedMessageHandler.IDelayedMessage;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncWalletCap implements IMessage, IMessageHandler<MessageServerSyncWalletCap, IMessage>, IDelayedMessage {

    private int entityID;
    private NBTTagCompound compound;
    
    public MessageServerSyncWalletCap() {
    }
    
    public MessageServerSyncWalletCap(int entityID, NBTTagCompound compound) {
        this.entityID = entityID;
        this.compound = compound;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        ByteBufUtils.writeTag(buf, compound);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        compound = ByteBufUtils.readTag(buf);
    }


    @Override
    public IMessage onMessage(MessageServerSyncWalletCap message, MessageContext ctx) {
        DelayedMessageHandler.addDelayedMessage(message);
        return null;
    }

    @Override
    public boolean isReady() {
        if (Minecraft.getMinecraft().world != null) {
            return Minecraft.getMinecraft().world.getEntityByID(entityID) != null;
        }
        return false;
    }

    @Override
    public void onDelayedMessage() {
        if (Minecraft.getMinecraft().world != null) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);
            if (entity != null && entity instanceof EntityLivingBase) {
                ICurrencyCapability wallet = CurrencyCapability.get((EntityLivingBase) entity);
                if (wallet != null) {
                    CurrencyCapability.WALLET_CAP.getStorage().readNBT(CurrencyCapability.WALLET_CAP, wallet, null, compound);
                }
            } else {
                RpgEconomy.getLogger().warn(String.format("Failed to get entity with %d when updating IWardrobeCapability.", entityID));
            }
        }
    }
}
