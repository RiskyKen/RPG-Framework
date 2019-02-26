package moe.plushie.rpgeconomy.currency.common.command;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpgeconomy.core.common.command.ModCommand;
import moe.plushie.rpgeconomy.core.common.command.ModSubCommands;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandCurrency extends ModSubCommands {

    public CommandCurrency(ModCommand parent) {
        super(parent, "currency");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RpgEconomy.getProxy().getCurrencyManager().reload(true);
            }
        }));
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // TODO Auto-generated method stub
        super.execute(server, sender, args);
    }
}
