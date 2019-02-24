package moe.plushie.rpgeconomy.core.common.network;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiMailBox;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncWalletCap;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private int packetId = 0;

    public PacketHandler() {
        // Client packets.
        registerMessage(MessageClientGuiMailBox.class, MessageClientGuiMailBox.class, Side.SERVER);
        registerMessage(MessageClientGuiButton.class, MessageClientGuiButton.class, Side.SERVER);

        // Server packets.
        registerMessage(MessageServerSyncWalletCap.class, MessageServerSyncWalletCap.class, Side.CLIENT);
    }

    private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
