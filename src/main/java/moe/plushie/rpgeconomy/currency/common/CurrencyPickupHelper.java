package moe.plushie.rpgeconomy.currency.common;

import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurrencyPickupHelper {

    public CurrencyPickupHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        ItemStack stack = event.getItem().getItem();
        EntityPlayer player = event.getEntityPlayer();

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
                            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                            //player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                            event.getItem().setDead();
                        }
                    }
                }

            }
        }
    }
}
