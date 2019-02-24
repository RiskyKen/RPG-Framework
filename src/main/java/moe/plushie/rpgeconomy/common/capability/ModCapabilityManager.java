package moe.plushie.rpgeconomy.common.capability;

import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.common.capability.currency.CurrencyCapability;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class ModCapabilityManager {

    private static final ResourceLocation KEY_CURRENCY_PROVIDER = new ResourceLocation(LibModInfo.ID, "currency_provider");

    private ModCapabilityManager() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ICurrencyCapability.class, new CurrencyCapability.Storage(), new CurrencyCapability.Factory());
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getObject();
        event.addCapability(KEY_CURRENCY_PROVIDER, new CurrencyCapability.Provider());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        ICurrencyCapability wallet = CurrencyCapability.get(event.player);
        if (wallet != null) {
            wallet.syncToOwner((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        World world = event.getEntityPlayer().getEntityWorld();
        if (event.isWasDeath()) {
            NBTBase nbt = null;
            ICurrencyCapability walletOld = CurrencyCapability.get(event.getOriginal());
            ICurrencyCapability walletNew = CurrencyCapability.get(event.getEntityPlayer());

            IStorage<ICurrencyCapability> storageWallet = CurrencyCapability.WALLET_CAP.getStorage();
            nbt = storageWallet.writeNBT(CurrencyCapability.WALLET_CAP, walletOld, null);
            storageWallet.readNBT(CurrencyCapability.WALLET_CAP, walletNew, null, nbt);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent event) {
        // Called after onPlayerClone. Used to sync after death.
        if (!event.isEndConquered()) {
            ICurrencyCapability wallet = CurrencyCapability.get(event.player);
            if (wallet != null) {
                wallet.syncToOwner((EntityPlayerMP) event.player);
            }
        }
    }
}
