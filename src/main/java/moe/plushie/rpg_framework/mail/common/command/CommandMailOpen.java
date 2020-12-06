package moe.plushie.rpg_framework.mail.common.command;

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
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandMailOpen extends ModCommand {

    public CommandMailOpen(ModCommand parent, String name) {
        super(parent, name);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // rpg mail open <"mail system"> [player target] [player source]
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        int identifierArgCount = 1;
        if (!args[getParentCount()].startsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        String stringIdentifier = args[getParentCount()];
        for (int i = getParentCount() + 1; i < args.length; i++) {
            if (stringIdentifier.endsWith("\"")) {
                break;
            }
            stringIdentifier += args[i];
            identifierArgCount++;
        }
        if (stringIdentifier.length() < 3) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        if (!stringIdentifier.endsWith("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        stringIdentifier = stringIdentifier.substring(1, stringIdentifier.length() - 1);
        
        IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString(stringIdentifier));
        
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
        

        if (mailSystem == null | playerTarget == null | playerSource == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        int index = RPGFramework.getProxy().getMailSystemManager().getMailSystemIndex(mailSystem);
        FMLNetworkHandler.openGui(playerTarget, RPGFramework.getInstance(), EnumGuiId.MAIL_COMMAND.ordinal(), server.getEntityWorld(), index, sourcePlayer.getId(), 0);
    }
}
