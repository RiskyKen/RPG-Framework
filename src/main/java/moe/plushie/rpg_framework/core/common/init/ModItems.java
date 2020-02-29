package moe.plushie.rpg_framework.core.common.init;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.common.items.ItemCommand;
import moe.plushie.rpg_framework.currency.common.items.ItemCurrency;
import moe.plushie.rpg_framework.currency.common.items.ItemWallet;
import moe.plushie.rpg_framework.loot.common.items.ItemBasicLootBag;
import moe.plushie.rpg_framework.loot.common.items.ItemLootBag;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModItems {
    
    public static final ArrayList<Item> ITEM_LIST = new ArrayList<Item>();
    
    public static final Item CURRENCY = new ItemCurrency();
    public static final Item WALLET = new ItemWallet();
    public static final Item LOOT_BAG = new ItemLootBag();
    public static final Item BASIC_LOOT_BAG = new ItemBasicLootBag();
    public static final Item COMMAND = new ItemCommand();
    
    public ModItems() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();
        for (int i = 0; i < ITEM_LIST.size(); i++) {
            reg.register(ITEM_LIST.get(i));
        }
    }
}
