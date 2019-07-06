package moe.plushie.rpgeconomy.core.common.handler;

import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class ConfigSyncHandler {

    private ConfigSyncHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncConfig(ConfigHandler.options), (EntityPlayerMP) event.player);
    }
}
