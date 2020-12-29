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
            // return getListOfStringsMatchingLastWord(args, RpgEconomy.getProxy().getBankManager().getBanks());
        }
        if (args.length == getParentCount() + 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // rpg bank open <"bank"> [player source] [player target]
        args = mergeArgs(args);

        // Check for missing args.
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IdentifierString identifierString = new IdentifierString(args[getParentCount()]);
        IBank bank = ModuleBank.getBankManager().getBank(identifierString);

        // Players bank that will be opened.
        GameProfile playerSource = null;
        // Player we want to display to.
        EntityPlayer playerTarget = null;

        if (args.length > getParentCount() + 1) {
            playerSource = new GameProfile(null, args[getParentCount() + 1]);

        } else {
            playerSource = getCommandSenderAsPlayer(sender).getGameProfile();
        }

        if (args.length > getParentCount() + 2) {
            playerTarget = getPlayer(server, sender, args[getParentCount() + 2]);
        } else {
            playerTarget = getCommandSenderAsPlayer(sender);
        }

        DBPlayer sourcePlayer = TablePlayers.getPlayer(playerSource);

        if (bank == null | playerTarget == null | playerSource == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        int index = ModuleBank.getBankManager().getBankIndex(bank);
        FMLNetworkHandler.openGui(playerTarget, RPGFramework.getInstance(), EnumGuiId.BANK_COMMAND.ordinal(), server.getEntityWorld(), index, sourcePlayer.getId(), playerTarget.getEntityId());
    }
}
