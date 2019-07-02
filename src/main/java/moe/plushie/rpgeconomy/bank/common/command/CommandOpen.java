package moe.plushie.rpgeconomy.bank.common.command;

import java.util.List;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.ModCommand;
import moe.plushie.rpgeconomy.core.common.lib.LibGuiIds;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandOpen extends ModCommand {

    public CommandOpen(ModCommand parent, String name) {
        super(parent, name);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            //return getListOfStringsMatchingLastWord(args, RpgEconomy.getProxy().getBankManager().getBanks());
        }
        if (args.length == getParentCount() + 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        
        
        if (!args[getParentCount()].startsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        String bankIdentifier = args[getParentCount()];
        
        
        for (int i = getParentCount() + 1; i < args.length; i++) {
            if (bankIdentifier.endsWith("\"")) {
                break;
            }
            bankIdentifier += args[i];
        }
        
        if (bankIdentifier.length() < 3) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        if (!bankIdentifier.endsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        bankIdentifier = bankIdentifier.substring(1, bankIdentifier.length() - 1);
        
        
        
        RpgEconomy.getLogger().info("bankIdentifier:" + bankIdentifier);
        
        IBank bank = RpgEconomy.getProxy().getBankManager().getBank(bankIdentifier);
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        
        //DBPlayer dbPlayer = Database.PLAYERS_TABLE.getPlayer(player);

        if (args.length > getParentCount()) {
            //playerId = Database.PLAYERS_TABLE.getPlayerId(args[getParentCount() + 1]);
        }
        
        /*if (playerId == -1) {
            throw new PlayerNotFoundException("commands.generic.player.unspecified");
        }
        
        if (bank == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }*/
        
        int index = RpgEconomy.getProxy().getBankManager().getBankIndex(bank);
        FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), LibGuiIds.BANK, server.getEntityWorld(), index, 0, 0);
    }
}
