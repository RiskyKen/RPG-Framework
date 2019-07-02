package moe.plushie.rpgeconomy.core.common.command;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpgeconomy.core.database.SQLiteDriver;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDev extends ModSubCommands {

	public CommandDev(ModCommand parent) {
		super(parent, "dev");
        addSubCommand(new CommandExecute(this, "sql-test", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            	RpgEconomy.getLogger().info("Updating database.");
            	SQLiteDriver.executeUpdate(
            			"DROP TABLE IF EXISTS TEST",
            			"CREATE TABLE TEST (id INTEGER, name STRING)",
            			"INSERT INTO TEST VALUES(1, 'Test 1')",
            			"INSERT INTO TEST VALUES(2, 'Test 2')");
            	
            	RpgEconomy.getLogger().info("Query database.");
            	ArrayList<String> result = SQLiteDriver.executeQueryArrayList("SELECT * FROM TEST");
            	RpgEconomy.getLogger().info(result.toString());
            }
        }));
        
        addSubCommand(new CommandExecute(this, "create-player-table", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            	RpgEconomy.getLogger().info("Creating table players.");
            	SQLiteDriver.executeUpdate(
            			"DROP TABLE IF EXISTS players",
            			"CREATE TABLE players "
            			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            			+ "uuid VARCHAR(36) NOT NULL,"
            			+ "username VARCHAR(80) NOT NULL,"
            			+ "first_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
            			+ "last_seen DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");
            }
        }));
        
        addSubCommand(new CommandExecute(this, "add-player", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            	EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            	String sql = "INSERT INTO players (id, uuid, username, first_seen, last_seen) VALUES (NULL, '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            	sql = String.format(sql, player.getGameProfile().getId().toString(), player.getGameProfile().getName());
            	RpgEconomy.getLogger().info(sql);
            	SQLiteDriver.executeUpdate(sql);
            }
        }));
        
        addSubCommand(new CommandExecute(this, "print-player-table", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            	RpgEconomy.getLogger().info("Query database.");
            	ArrayList<String> result = SQLiteDriver.executeQueryArrayList("SELECT * FROM players");
            	RpgEconomy.getLogger().info(result.toString());
            	EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            	for (String s : result) {
            		player.sendMessage(new TextComponentString(s));
            	}
            }
        }));
        
        addSubCommand(new CommandExecute(this, "sql", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            	if (args.length > 2) {
                	String sql = args[2];
                	for (int i = 3; i < args.length; i++) {
                		sql += " " + args[i];
                	}
                	RpgEconomy.getLogger().info("Query database.");
                	RpgEconomy.getLogger().info(sql);
                	ArrayList<String> result = SQLiteDriver.executeQueryArrayList(sql);
                	EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                	player.sendMessage(new TextComponentString("Query result"));
                	for (String s : result) {
                		player.sendMessage(new TextComponentString(s));
                	}
            	}
            }
        }));
	}
}
