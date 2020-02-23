package moe.plushie.rpg_framework.core.common.command;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.DatebaseTable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDev extends ModSubCommands {

    public CommandDev(ModCommand parent) {
        super(parent, "dev");
        addSubCommand(new CommandExecute(this, "sql", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length > 3) {
                    DatebaseTable datebaseTable = DatebaseTable.valueOf(args[2].toUpperCase());
                    String sql = args[3];
                    for (int i = 4; i < args.length; i++) {
                        sql += " " + args[i];
                    }
                    RPGFramework.getLogger().info("Query database.");
                    RPGFramework.getLogger().info(sql);
                    ArrayList<String> result = DatabaseManager.executeQueryArrayList(datebaseTable, sql);
                    EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                    player.sendMessage(new TextComponentString("Query result"));
                    for (String s : result) {
                        player.sendMessage(new TextComponentString(s));
                    }
                }
            }
        }));
        addSubCommand(new CommandExecute(this, "make_example_files", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RPGFramework.getProxy().createExampleFiles();
            }
        }));
    }
}
