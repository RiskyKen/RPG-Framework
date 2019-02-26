package moe.plushie.rpgeconomy.core.common.command;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandExecute extends ModCommand {

    private final ICommandExecute commandExecute;
    
    public CommandExecute(ModCommand parent, String name, ICommandExecute commandExecute) {
        super(parent, name);
        this.commandExecute = commandExecute;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        RpgEconomy.getLogger().info(getUsage(sender));
        commandExecute.execute(server, sender, args);
    }
    
    public static interface ICommandExecute {

        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    }
}
