package moe.plushie.rpg_framework.core.common.command;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.rpg_framework.bank.common.command.CommandBank;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerCommand;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerCommand.ServerCommandType;
import moe.plushie.rpg_framework.currency.common.command.CommandCurrency;
import moe.plushie.rpg_framework.itemData.CommandItemData;
import moe.plushie.rpg_framework.loot.common.command.CommandLoot;
import moe.plushie.rpg_framework.mail.common.command.CommandMail;
import moe.plushie.rpg_framework.shop.common.command.CommandShop;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandRpg extends ModSubCommands {

    public CommandRpg() {
        super(null, LibModInfo.ID);
        /*addSubCommand(new CommandExecute(this, "manager", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), EnumGuiId.MANAGER.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));*/
        addSubCommand(new CommandCurrency(this));
        addSubCommand(new CommandMail(this));
        addSubCommand(new CommandShop(this));
        addSubCommand(new CommandDev(this));
        addSubCommand(new CommandBank(this));
        addSubCommand(new CommandIntegration(this));
        addSubCommand(new CommandLoot(this));
        addSubCommand(new CommandItemData(this));
        addSubCommand(new CommandExecute(this, "open_folder", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerCommand(ServerCommandType.OPEN_PACK_FOLDER), player);
            }
        }));
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<String>();
        aliases.add("rpg");
        return aliases;
    }
}
