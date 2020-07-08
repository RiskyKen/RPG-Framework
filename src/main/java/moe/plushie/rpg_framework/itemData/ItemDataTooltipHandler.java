package moe.plushie.rpg_framework.itemData;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.shop.client.gui.GuiShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemDataTooltipHandler {

    public static Cache<ItemStack, IItemData> cache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterAccess(2, TimeUnit.SECONDS)
            .<ItemStack, IItemData>build();

    public static HashSet<ItemStack> loadingSet = new HashSet<ItemStack>();

    public ItemDataTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiShop) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) {
            return;
        }
        if (Minecraft.getMinecraft().world == null) {
            return;
        }
        IItemData itemData = cache.getIfPresent(event.getItemStack());
        if (itemData == null) {
            synchronized (loadingSet) {
                if (!loadingSet.contains(itemStack)) {
                    loadingSet.add(itemStack);
                    ModuleItemData.getManager().getItemDataAsync(itemStack, new Loader(itemStack));
                }
            }
            event.getToolTip().add(I18n.format("tooltip." + LibModInfo.ID.toLowerCase() + ":value_loading"));
            return;
        }

        if (!itemData.isDataMissing()) {
            if (itemData.getValue().hasWalletCost()) {
                IWallet wallet = itemData.getValue().getWalletCosts()[0];
                String value = String.format(wallet.getCurrency().getDisplayFormat(), wallet.getAmount());
                event.getToolTip().add(value);
            }
        } else {
            event.getToolTip().add(I18n.format("tooltip." + LibModInfo.ID.toLowerCase() + ":value_none"));
        }
    }

    private static class Loader implements FutureCallback<IItemData> {

        private final ItemStack itemStack;

        public Loader(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public void onSuccess(IItemData result) {
            synchronized (loadingSet) {
                loadingSet.remove(itemStack);
                cache.put(itemStack, result);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            synchronized (loadingSet) {
                loadingSet.remove(itemStack);
                cache.put(itemStack, ItemData.ITEM_DATA_MISSING);
            }
        }
    }
}
