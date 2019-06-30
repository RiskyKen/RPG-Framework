package moe.plushie.rpgeconomy.core.common.init;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds {

    public static final ArrayList<SoundEvent> SOUND_LIST = new ArrayList<SoundEvent>();
    
    public static final SoundEvent COIN_DEPOSIT = createSoundEvent("coin_deposit");
    public static final SoundEvent COIN_WITHDRAW = createSoundEvent("coin_withdraw");
    public static final SoundEvent WALLET_CLOSE = createSoundEvent("wallet_close");
    public static final SoundEvent WALLET_OPEN = createSoundEvent("wallet_open");
    
    public ModSounds() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private static SoundEvent createSoundEvent(String soundName) {
        ResourceLocation resourceLocation = new ResourceLocation(LibModInfo.ID, soundName);
        return new SoundEvent(resourceLocation).setRegistryName(resourceLocation);
    }
    
    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> reg = event.getRegistry();
        for (int i = 0; i < SOUND_LIST.size(); i++) {
            reg.register(SOUND_LIST.get(i));
        }
    }
}
