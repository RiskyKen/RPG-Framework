package moe.plushie.rpgeconomy.core.common.command;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.rpgeconomy.bank.common.command.CommandBank;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.currency.common.command.CommandCurrency;
import moe.plushie.rpgeconomy.mail.common.command.CommandMail;
import moe.plushie.rpgeconomy.shop.common.command.CommandShop;

public class CommandRpg extends ModSubCommands {

    public CommandRpg() {
        super(null, LibModInfo.ID);
        addSubCommand(new CommandCurrency(this));
        addSubCommand(new CommandMail(this));
        addSubCommand(new CommandShop(this));
        addSubCommand(new CommandDev(this));
        addSubCommand(new CommandBank(this));
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<String>();
        aliases.add("rpg");
        return aliases;
    }
}
