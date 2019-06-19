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
                RpgEconomy.getProxy().getShopManager().reload();
            }
        }));
    }
}
