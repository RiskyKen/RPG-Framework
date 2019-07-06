package moe.plushie.rpgeconomy.core.common.command;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.rpgeconomy.bank.common.command.CommandBank;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpgeconomy.core.common.lib.EnumGuiId;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.currency.common.command.CommandCurrency;
import moe.plushie.rpgeconomy.mail.common.command.CommandMail;
import moe.plushie.rpgeconomy.shop.common.command.CommandShop;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandRpg extends ModSubCommands {

    public CommandRpg() {
        super(null, LibModInfo.ID);
        addSubCommand(new CommandExecute(this, "manager", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), EnumGuiId.MANAGER.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));
        addSubCommand(new CommandCurrency(this));
        addSubCommand(new CommandMail(this));
        addSubCommand(new CommandShop(this));
        addSubCommand(new CommandDev(this));
        addSubCommand(new CommandBank(this));
        addSubCommand(new CommandIntegration(this));
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<String>();
        aliases.add("rpg");
        return aliases;
    }
}
