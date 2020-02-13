package moe.plushie.rpg_framework.core.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class DatabaseExecutor {

    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    private DatabaseExecutor() {
    }

    public static void loadShop(IShopLoadCallback callback, IIdentifier identifier) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                IShop shop = RPGFramework.getProxy().getShopManager().getShop(identifier);
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onShopLoad(shop);
                    }
                });
            }
        });
    }

    public static interface IShopLoadCallback {
        public void onShopLoad(IShop shop);
    }
}
