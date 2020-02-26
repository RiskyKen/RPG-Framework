package moe.plushie.rpg_framework.currency.common.command;

import java.util.List;

import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandCurrencyAdd extends ModCommand {

    public CommandCurrencyAdd(ModCommand parent, String name) {
        super(parent, name);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, RPGFramework.getProxy().getCurrencyManager().getCurrencyNames());
        }
        if (args.length == getParentCount() + 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String currencyText = args[getParentCount()];
        String playerText = args[getParentCount() + 1];
        String amountText = args[getParentCount() + 2];
        
        ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getCurrency(new IdentifierString(currencyText));
        EntityPlayerMP entityPlayer = getPlayer(server, sender, playerText);
        int amount = parseInt(amountText, 0);
        
        
        if (currency == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        ICurrencyCapability currencyCap = CurrencyCapability.get(entityPlayer);
        if (currencyCap == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        IWallet wallet = currencyCap.getWallet(currency);
        wallet.addAmount(amount);
        currencyCap.syncToOwner(entityPlayer, true);
    }
}
