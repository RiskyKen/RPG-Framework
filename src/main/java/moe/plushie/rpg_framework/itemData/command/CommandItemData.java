package moe.plushie.rpg_framework.itemData.command;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.itemData.ModuleItemData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandItemData extends ModCommand {

    public CommandItemData(ModCommand parent) {
        super(parent, "item_data");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer entityPlayer = getCommandSenderAsPlayer(sender);
        ItemStack itemStack = entityPlayer.getHeldItemMainhand();
        int value = parseInt(args[getParentCount()]);

        boolean meta = true;
        if (args.length - 1 > getParentCount()) {
            meta = parseBoolean(args[getParentCount() + 1]);
        }

        boolean override = false;
        if (args.length - 1 > getParentCount() + 1) {
            override = parseBoolean(args[getParentCount() + 2]);
        }

        if (!itemStack.isEmpty()) {
            IItemMatcher itemMatcher = new ItemMatcherStack(itemStack, meta, true);
            ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getDefault();
            Wallet wallet = new Wallet(currency, value);

            if (override) {
                ModuleItemData.getManager().setItemOverrideValueAsync(itemMatcher, new Cost(wallet));
            } else {
                IItemData itemData = ModuleItemData.getManager().getItemData(itemMatcher);
                itemData = itemData.setValue(new Cost(wallet));
                ModuleItemData.getManager().setItemDataAsync(itemMatcher, itemData);
            }
        }
    }
}
