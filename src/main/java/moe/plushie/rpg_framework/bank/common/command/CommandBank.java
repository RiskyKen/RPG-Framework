package moe.plushie.rpg_framework.bank.common.command;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.command.ModSubCommands;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandBank extends ModSubCommands {

    public CommandBank(ModCommand parent) {
        super(parent, "bank");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RPGFramework.getProxy().getBankManager().reload(true);
            }
        }));
        addSubCommand(new CommandOpenBank(this, "open"));
    }
}
