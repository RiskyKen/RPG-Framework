package moe.plushie.rpgeconomy.loot.common.command;

import moe.plushie.rpgeconomy.api.loot.ILootTable;
import moe.plushie.rpgeconomy.api.loot.ILootTablePool;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute;
import moe.plushie.rpgeconomy.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpgeconomy.core.common.command.ModCommand;
import moe.plushie.rpgeconomy.core.common.command.ModSubCommands;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.lib.EnumGuiId;
import moe.plushie.rpgeconomy.core.database.TableLootPools;
import moe.plushie.rpgeconomy.core.database.TableLootTables;
import moe.plushie.rpgeconomy.loot.common.LootTableItem;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandLoot extends ModSubCommands {

    public CommandLoot(ModCommand parent) {
        super(parent, "loot");
        addSubCommand(new CommandExecute(this, "db_test", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                ILootTable lootTable = TableLootTables.createNew("Test Table", "Testing Category");
                
                
                for (int i = 0; i < 10; i++) {
                    ILootTablePool pool = TableLootPools.createNew("Test Pool", "Testing Category");
                    lootTable.getLootPools().add(pool.getIdentifier());
                    pool.getPoolItems().add(new LootTableItem(new ItemStack(ModBlocks.SHOP), 1));
                    pool.getPoolItems().add(new LootTableItem(new ItemStack(Blocks.ANVIL), 1));
                    TableLootPools.update(pool);
                }
                
                TableLootTables.update(lootTable);
            }
        }));
        addSubCommand(new CommandExecute(this, "open_loot_editor", new ICommandExecute() {
            
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), EnumGuiId.LOOT_EDITOR_COMMAND.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));
    }
}
