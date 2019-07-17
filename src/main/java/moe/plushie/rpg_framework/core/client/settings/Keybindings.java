package moe.plushie.rpg_framework.core.client.settings;

import org.lwjgl.input.Keyboard;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.lib.LibModKeys;
import moe.plushie.rpg_framework.core.common.lib.LibModKeys.ModKey;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientKeyPress;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = LibModInfo.ID, value = { Side.CLIENT })
@SideOnly(Side.CLIENT)
public final class Keybindings {

    public static final KeyBinding KEY_OPEN_WALLET_1 = new KeyBinding(ModKey.OPEN_WALLET_1.getFullName(), KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, LibModKeys.CATEGORY);
    public static final KeyBinding KEY_OPEN_WALLET_2 = new KeyBinding(ModKey.OPEN_WALLET_2.getFullName(), KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, LibModKeys.CATEGORY);
    
    private Keybindings() {
    }
    
    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(KEY_OPEN_WALLET_1);
        ClientRegistry.registerKeyBinding(KEY_OPEN_WALLET_2);
    }
    
    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (KEY_OPEN_WALLET_1.isPressed()) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientKeyPress(ModKey.OPEN_WALLET_1));
        }
        if (KEY_OPEN_WALLET_2.isPressed()) {
            PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientKeyPress(ModKey.OPEN_WALLET_2));
        }
    }
}
