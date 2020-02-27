package moe.plushie.rpg_framework.itemData;

import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Wallet;
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
        
        if (!itemStack.isEmpty()) {
            IItemData itemData = ItemData.createEmpty();
            ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getDefault();
            Wallet wallet = new Wallet(currency, value);
            itemData = itemData.setValue(new Cost(wallet));
            ModuleItemData.getManager().setItemDataAsync(new ItemMatcherStack(itemStack, meta, true), itemData);
        }
    }
}
