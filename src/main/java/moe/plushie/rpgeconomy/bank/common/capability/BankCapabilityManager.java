package moe.plushie.rpgeconomy.bank.common.capability;

import moe.plushie.rpgeconomy.api.bank.IBankCapability;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class BankCapabilityManager {
    
    private static final ResourceLocation KEY_BANK_PROVIDER = new ResourceLocation(LibModInfo.ID, "bank_provider");

    private BankCapabilityManager() {
    }
    
    public static void register() {
        CapabilityManager.INSTANCE.register(IBankCapability.class, new BankCapability.Storage(), new BankCapability.Factory());
    }
    
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getObject();
        event.addCapability(KEY_BANK_PROVIDER, new BankCapability.Provider(player));
    }
}
