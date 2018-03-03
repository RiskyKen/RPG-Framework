package moe.plushie.rpgeconomy.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;

public class PacketHandler {
    
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private int packetId = 0;
    
    public PacketHandler() {
        //registerMessage(MessageClientGuiColourUpdate.class, MessageClientGuiColourUpdate.class, Side.SERVER);
    }
    
    private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
