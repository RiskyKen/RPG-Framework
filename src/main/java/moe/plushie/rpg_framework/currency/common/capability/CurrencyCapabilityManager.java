package moe.plushie.rpg_framework.currency.common.capability;

import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class CurrencyCapabilityManager {

    private static final ResourceLocation KEY_CURRENCY_PROVIDER = new ResourceLocation(LibModInfo.ID, "currency_provider");

    private CurrencyCapabilityManager() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ICurrencyCapability.class, new CurrencyCapability.Storage(), new CurrencyCapability.Factory());
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) {
            return;
        }
        event.addCapability(KEY_CURRENCY_PROVIDER, new CurrencyCapability.Provider());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        ICurrencyCapability currencyCap = CurrencyCapability.get(event.player);
        if (currencyCap != null) {
            currencyCap.syncToOwner((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            NBTBase nbt = null;
            ICurrencyCapability currencyCapOld = CurrencyCapability.get(event.getOriginal());
            ICurrencyCapability currencyCapNew = CurrencyCapability.get(event.getEntityPlayer());

            IStorage<ICurrencyCapability> currencyStorage = CurrencyCapability.WALLET_CAP.getStorage();
            nbt = currencyStorage.writeNBT(CurrencyCapability.WALLET_CAP, currencyCapOld, null);
            currencyStorage.readNBT(CurrencyCapability.WALLET_CAP, currencyCapNew, null, nbt);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent event) {
        // Called after onPlayerClone. Used to sync after death.
        if (!event.isEndConquered()) {
            ICurrencyCapability currencyCap = CurrencyCapability.get(event.player);
            if (currencyCap != null) {
                currencyCap.syncToOwner((EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerChangedDimensionEvent event) {
        ICurrencyCapability currencyCap = CurrencyCapability.get(event.player);
        if (currencyCap != null) {
            currencyCap.syncToOwner((EntityPlayerMP) event.player);
        }
    }
}
