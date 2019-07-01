package moe.plushie.rpgeconomy.core.common.network;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiMailBox;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientKeyPress;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientRequestSync;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerShop;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncBankAccount;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncBanks;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncCurrencies;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncMailSystems;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncShops;
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
        registerMessage(MessageClientKeyPress.class, MessageClientKeyPress.class, Side.SERVER);
        registerMessage(MessageClientGuiShopUpdate.class, MessageClientGuiShopUpdate.class, Side.SERVER);
        registerMessage(MessageClientRequestSync.class, MessageClientRequestSync.class, Side.SERVER);

        // Server packets.
        registerMessage(MessageServerSyncWalletCap.class, MessageServerSyncWalletCap.class, Side.CLIENT);
        registerMessage(MessageServerSyncCurrencies.class, MessageServerSyncCurrencies.class, Side.CLIENT);
        registerMessage(MessageServerSyncMailSystems.class, MessageServerSyncMailSystems.class, Side.CLIENT);
        registerMessage(MessageServerShop.class, MessageServerShop.class, Side.CLIENT);
        registerMessage(MessageServerSyncShops.class, MessageServerSyncShops.class, Side.CLIENT);
        registerMessage(MessageServerSyncBanks.class, MessageServerSyncBanks.class, Side.CLIENT);
        registerMessage(MessageServerSyncBankAccount.class, MessageServerSyncBankAccount.class, Side.CLIENT);
    }

    private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
