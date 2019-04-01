package moe.plushie.rpgeconomy.shop.common.command;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute;
import moe.plushie.rpgeconomy.core.common.command.ModCommand;
import moe.plushie.rpgeconomy.core.common.command.ModSubCommands;
import moe.plushie.rpgeconomy.core.database.SQLiteDriver;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandShop extends ModSubCommands {

    public CommandShop(ModCommand parent) {
        super(parent, "shop");
        addSubCommand(new CommandExecute(this, "reload", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                RpgEconomy.getProxy().getMailSystemManager().reload(true);
            }
        }));
        
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
            	ArrayList<String> result = SQLiteDriver.executeQuery("SELECT * FROM TEST");
            	RpgEconomy.getLogger().info(result.toString());
            }
        }));
    }
}
