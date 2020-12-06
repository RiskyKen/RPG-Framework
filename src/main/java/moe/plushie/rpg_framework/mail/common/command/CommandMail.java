package moe.plushie.rpg_framework.mail.common.command;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.command.ModSubCommands;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandMail extends ModSubCommands {

    public CommandMail(ModCommand parent) {
        super(parent, "mail");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RPGFramework.getProxy().getMailSystemManager().reload(true);
            }
        }));
        addSubCommand(new CommandMailOpen(this, "open"));
    }
}
