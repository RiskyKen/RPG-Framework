package moe.plushie.rpg_framework.loot.common.inventory;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.common.inventory.ModContainer;
import moe.plushie.rpg_framework.core.database.TableLootPools;
import moe.plushie.rpg_framework.loot.common.LootTableHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerLootEditor extends ModContainer {

    private final EntityPlayer player;
    
    public ContainerLootEditor(EntityPlayer player) {
        super(player.inventory);
        this.player = player;
        addPlayerSlots(8, 8);
    }

    public void lootPoolAdd(String name, String category) {
        TableLootPools.createNew(name, category);
        sendPoolsToListeners();
    }

    public void lootPoolEdit(ILootTablePool pool) {
        TableLootPools.update(pool);
        sendPoolsToListeners();
    }

    public void lootPoolRemove(IIdentifier identifier) {
        TableLootPools.delete(identifier);
        sendPoolsToListeners();
    }

    public void lootPoolRename(IIdentifier identifier, String name, String category) {
        TableLootPools.rename(identifier, name, category);
        sendPoolsToListeners();
    }
    
    public void lootPoolRequest(IIdentifier identifier) {
        LootTableHelper.sendPoolToClient(identifier, (EntityPlayerMP)player);
    }
    
    private void sendPoolsToListeners() {
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                LootTableHelper.syncLootPoolsToClient((EntityPlayerMP) listeners.get(j));
            }
        }
    }
    
    private void sendTablesToListeners() {
        for (int j = 0; j < this.listeners.size(); ++j) {
            if (listeners.get(j) instanceof EntityPlayerMP) {
                LootTableHelper.syncLootTablesToClient((EntityPlayerMP) listeners.get(j));
            }
        }
    }
}
