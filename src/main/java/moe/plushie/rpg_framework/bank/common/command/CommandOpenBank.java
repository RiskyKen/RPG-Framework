package moe.plushie.rpg_framework.bank.common.command;

import java.util.List;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandOpenBank extends ModCommand {

    public CommandOpenBank(ModCommand parent, String name) {
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
        // /rpg bank open "<bank> [playerShow] [playerBank]"
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        int identifierArgCount = 1;
        if (!args[getParentCount()].startsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        String bankIdentifier = args[getParentCount()];
        for (int i = getParentCount() + 1; i < args.length; i++) {
            if (bankIdentifier.endsWith("\"")) {
                break;
            }
            bankIdentifier += args[i];
            identifierArgCount++;
        }
        if (bankIdentifier.length() < 3) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        if (!bankIdentifier.endsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        bankIdentifier = bankIdentifier.substring(1, bankIdentifier.length() - 1);
        
        IBank bank = RPGFramework.getProxy().getBankManager().getBank(bankIdentifier);
        
        
        EntityPlayer playerShow = getCommandSenderAsPlayer(sender);
        //EntityPlayer playerBank = getCommandSenderAsPlayer(sender);
        
        if (args.length > identifierArgCount + getParentCount()) {
            playerShow = getPlayer(server, sender, args[getParentCount() + identifierArgCount]);
        }
        /*
        if (args.length > identifierArgCount + getParentCount() + 1) {
            RPGFramework.getLogger().info("Setting player");
            playerBank = getPlayer(server, sender, args[getParentCount() + identifierArgCount + 1]);
        }
        */
        
        if (args.length + identifierArgCount > getParentCount()) {
            //playerId = Database.PLAYERS_TABLE.getPlayerId(args[getParentCount() + 1]);
        }

        if (bank == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        int index = RPGFramework.getProxy().getBankManager().getBankIndex(bank);
        FMLNetworkHandler.openGui(playerShow, RPGFramework.getInstance(), EnumGuiId.BANK_COMMAND.ordinal(), server.getEntityWorld(), index, 0, 0);
    }
}
