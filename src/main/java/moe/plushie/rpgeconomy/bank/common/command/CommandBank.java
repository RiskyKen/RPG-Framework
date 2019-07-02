package moe.plushie.rpgeconomy.bank.common.command;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpgeconomy.core.common.command.ModCommand;
import moe.plushie.rpgeconomy.core.common.command.ModSubCommands;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandBank extends ModSubCommands {

    public CommandBank(ModCommand parent) {
        super(parent, "bank");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RpgEconomy.getProxy().getBankManager().reload(true);
            }
        }));
        addSubCommand(new CommandOpen(this, "open"));
    }
}
