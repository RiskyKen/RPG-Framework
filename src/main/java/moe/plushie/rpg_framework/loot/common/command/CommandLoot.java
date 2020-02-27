package moe.plushie.rpg_framework.loot.common.command;

import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.command.ModSubCommands;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.database.loot.TableLootPools;
import moe.plushie.rpg_framework.core.database.loot.TableLootTables;
import moe.plushie.rpg_framework.loot.common.LootTableItem;
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
                    ILootTablePool pool = TableLootPools.createNew("Test Pool " + (i + 1), "Testing Category");
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
                FMLNetworkHandler.openGui(player, RPGFramework.getInstance(), EnumGuiId.LOOT_EDITOR_COMMAND.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));
    }
}
