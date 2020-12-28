package moe.plushie.rpg_framework.bank.common.command;

import java.util.List;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.database.DBPlayer;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
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
        // /rpg bank open "<bank> [playerTarget] [playerSource]"
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
        
        IBank bank = ModuleBank.getBankManager().getBank(new IdentifierString(bankIdentifier));
        
        // Player we want to display to.
        EntityPlayer playerTarget = null;
        // Players bank that will be opened.
        GameProfile playerSource = null;
        DBPlayer sourcePlayer;
        //EntityPlayer playerBank = getCommandSenderAsPlayer(sender);
        
        if (args.length > identifierArgCount + getParentCount()) {
            playerTarget = getPlayer(server, sender, args[getParentCount() + identifierArgCount]);
            
        } else {
            playerTarget = getCommandSenderAsPlayer(sender);
        }
        playerSource = playerTarget.getGameProfile();
        
        
        if (args.length > identifierArgCount + getParentCount() + 1) {
            String sourceName = args[getParentCount() + identifierArgCount + 1];
            playerSource = new GameProfile(null, sourceName);
        }
        
        sourcePlayer = TablePlayers.getPlayer(playerSource);
        

        if (bank == null | playerTarget == null | playerSource == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        int index = ModuleBank.getBankManager().getBankIndex(bank);
        FMLNetworkHandler.openGui(playerTarget, RPGFramework.getInstance(), EnumGuiId.BANK_COMMAND.ordinal(), server.getEntityWorld(), index, sourcePlayer.getId(), 0);
    }
}
