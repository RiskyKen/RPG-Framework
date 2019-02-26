package moe.plushie.rpgeconomy.core.common.command;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.currency.common.command.CommandCurrency;
import moe.plushie.rpgeconomy.mail.common.command.CommandMail;

public class CommandRpg extends ModSubCommands {

    public CommandRpg() {
        super(null, LibModInfo.ID);
        addSubCommand(new CommandCurrency(this));
        addSubCommand(new CommandMail(this));
    }
}
