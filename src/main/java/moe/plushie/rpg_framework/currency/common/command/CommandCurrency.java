package moe.plushie.rpg_framework.currency.common.command;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.command.ModSubCommands;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandCurrency extends ModSubCommands {

    public CommandCurrency(ModCommand parent) {
        super(parent, "currency");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RPGFramework.getProxy().getCurrencyManager().reload(true);
            }
        }));
        addSubCommand(new CommandCurrencySet(this, "set"));
        addSubCommand(new CommandCurrencyAdd(this, "add"));
        addSubCommand(new CommandCurrencyRemove(this, "remove"));
    }
}
