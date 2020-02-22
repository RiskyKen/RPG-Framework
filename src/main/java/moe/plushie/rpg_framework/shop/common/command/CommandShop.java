package moe.plushie.rpg_framework.shop.common.command;

import moe.plushie.rpg_framework.core.common.command.CommandExecute;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.command.ModSubCommands;
import moe.plushie.rpg_framework.shop.ModuleShop;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandShop extends ModSubCommands {

    public CommandShop(ModCommand parent) {
        super(parent, "shop");
        addSubCommand(new CommandOpenShop(this, "open"));
        addSubCommand(new CommandExecute(this, "import_json", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                ModuleShop.getShopManager().importShopJson();
            }
        }));
        addSubCommand(new CommandExecute(this, "export_json", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                ModuleShop.getShopManager().exportShopJson();
            }
        }));
    }
}
