package moe.plushie.rpgeconomy.currency.common;

import java.util.HashMap;

import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.init.ModSounds;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurrencyPickupHelper {

    public static HashMap<EntityPlayer, Long> playerTimeMap = new HashMap<EntityPlayer, Long>();
    public static HashMap<EntityPlayer, Integer> playerComboMap = new HashMap<EntityPlayer, Integer>();
    
    public CurrencyPickupHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        ItemStack stack = event.getItem().getItem();
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        
        Currency[] currencies = RpgEconomy.getProxy().getCurrencyManager().getCurrencies();
        for (Currency currency : currencies) {
            if (!currency.getCurrencyWalletInfo().getPickupIntoWallet()) {
                continue;
            }
            if (currency.getCurrencyWalletInfo().getNeedItemToAccess() & !CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                continue;
            }
            for (CurrencyVariant variant : currency.getCurrencyVariants()) {
                if (variant.getItem().matches(stack)) {
                    ICurrencyCapability currencyCap = CurrencyCapability.get(player);
                    if (currencyCap != null) {
                        IWallet wallet = currencyCap.getWallet(currency);
                        if (wallet != null) {
                            wallet.addAmount(variant.getValue() * stack.getCount());
                            if (player instanceof EntityPlayerMP) {
                                currencyCap.syncToOwner((EntityPlayerMP) player);
                            }
                            event.setCanceled(true);
                            int combo = 0;
                            long lastTime = 0L;
                            if (playerTimeMap.containsKey(player)) {
                                lastTime = playerTimeMap.get(player);
                            }
                            if (world.getTotalWorldTime() <= lastTime + 20) {
                                combo = playerComboMap.get(player) + 1;
                            }
                            lastTime = world.getTotalWorldTime();
                            
                            playerComboMap.put(player, combo);
                            playerTimeMap.put(player, lastTime);
                            
                            combo = MathHelper.clamp(combo, 0, 20);
                            
                            player.getEntityWorld().playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, ModSounds.COIN_DEPOSIT, SoundCategory.PLAYERS, 0.3F + (float)combo * 0.01F, 0.6F + (float)combo * 0.025F);
                            event.getItem().setDead();
                        }
                    }
                }
            }
        }
    }
}
