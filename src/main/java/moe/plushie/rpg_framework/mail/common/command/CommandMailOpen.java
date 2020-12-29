package moe.plushie.rpg_framework.mail.common.command;

import java.util.List;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
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

public class CommandMailOpen extends ModCommand {

    public CommandMailOpen(ModCommand parent, String name) {
        super(parent, name);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // /rpg mail open <"mail system"> [player source] [player target]
        args = mergeArgs(args);

        // Check for missing args.
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IdentifierString identifierString = new IdentifierString(args[getParentCount()]);
        IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(identifierString);

        // Players mail that will be opened.
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

        if (mailSystem == null | playerTarget == null | playerSource == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        int index = RPGFramework.getProxy().getMailSystemManager().getMailSystemIndex(mailSystem);
        FMLNetworkHandler.openGui(playerTarget, RPGFramework.getInstance(), EnumGuiId.MAIL_COMMAND.ordinal(), server.getEntityWorld(), index, sourcePlayer.getId(), playerTarget.getEntityId());
    }
}
