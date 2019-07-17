package moe.plushie.rpg_framework.currency.common.inventory;

import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.common.config.ConfigHandler;
import moe.plushie.rpg_framework.core.common.init.ModSounds;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.common.inventory.ModInventory;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.CurrencyWalletHelper;
import moe.plushie.rpg_framework.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;

public class ContainerWallet extends ModContainer implements IButtonPress {

    private final EntityPlayer player;
    private final Currency currency;
    private final ICurrencyCapability currencyCap;
    private final IWallet wallet;
    private final ModInventory inventoryWallet;

    public ContainerWallet(EntityPlayer entityPlayer, Currency currency) {
        super(entityPlayer.inventory);

        this.player = entityPlayer;
        this.currency = currency;
        this.currencyCap = CurrencyCapability.get(entityPlayer);
        this.wallet = currencyCap.getWallet(currency);

        inventoryWallet = new ModInventory("wallet", currency.getCurrencyVariants().length);

        if (ConfigHandler.options.showPlayerInventoryInWalletGUI) {
            addPlayerSlots(8, 114);
        }

        int sizeX = 176;
        int slotSpacing = 1;
        int slotSize = 18;

        int halfSizeX = (int) ((float) sizeX / 2F);
        int slotCount = currency.getCurrencyVariants().length;
        int slotTotalWidth = (slotSize + slotSpacing) * slotCount - 1;
        int halfSlotTotalWidth = (int) ((float) slotTotalWidth / 2F);

        for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
            addSlotToContainer(new SlotCurrency(currency, currency.getCurrencyVariants()[i], inventoryWallet, i, halfSizeX - halfSlotTotalWidth + i * (slotSize + slotSpacing), 54));
            inventoryWallet.setInventorySlotContents(i, currency.getCurrencyVariants()[i].getItem().getItemStack().copy());
        }
        player.playSound(ModSounds.WALLET_OPEN, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
    }
    
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        player.playSound(ModSounds.WALLET_CLOSE, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
        super.onContainerClosed(playerIn);
    }

    @Override
    public void buttonPress(int buttonID) {
        boolean withdraw = false;
        if (buttonID < 0) {
            int amount = CurrencyWalletHelper.consumeAllFromInventory(currency, player.inventory);
            player.getEntityWorld().playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, ModSounds.COIN_DEPOSIT, SoundCategory.PLAYERS, 0.5F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
            wallet.addAmount(amount);
            currencyCap.syncToOwner((EntityPlayerMP) player);
            return;
        }
        if (buttonID >= currency.getCurrencyVariants().length) {
            withdraw = true;
        }
        buttonID = buttonID % currency.getCurrencyVariants().length;
        CurrencyVariant variant = currency.getCurrencyVariants()[buttonID];
        if (withdraw) {
            if (wallet.getAmount() >= variant.getValue()) {
                if (player.addItemStackToInventory(variant.getItem().getItemStack().copy())) {
                    wallet.setAmount(wallet.getAmount() - variant.getValue());
                    player.getEntityWorld().playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, ModSounds.COIN_WITHDRAW, SoundCategory.PLAYERS, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                    currencyCap.syncToOwner((EntityPlayerMP) player);
                }
            }
        } else {
            if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, variant.getValue(), true)) {
                CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, variant.getValue(), false);
                player.getEntityWorld().playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, ModSounds.COIN_DEPOSIT, SoundCategory.PLAYERS, 0.3F, 0.8F + (player.getRNG().nextFloat() * 0.4F));
                wallet.setAmount(wallet.getAmount() + variant.getValue());
                currencyCap.syncToOwner((EntityPlayerMP) player);
            }
        }
    }
}
