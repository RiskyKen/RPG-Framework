package moe.plushie.rpg_framework.loot.common.items;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.items.AbstractModItem;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.common.lib.LibItemNames;
import moe.plushie.rpg_framework.core.common.serialize.ItemStackSerialize;
import moe.plushie.rpg_framework.core.common.utils.UtilItems;
import moe.plushie.rpg_framework.loot.common.LootTableItem;
import moe.plushie.rpg_framework.loot.common.LootTableItemSerializer;
import moe.plushie.rpg_framework.loot.common.LootTablePool;
import moe.plushie.rpg_framework.loot.common.inventory.ContainerBasicLootBag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemBasicLootBag extends AbstractModItem {

    public ItemBasicLootBag() {
        super(LibItemNames.BASIC_LOOT_BAG);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {

            if (playerIn.isSneaking() & playerIn.isCreative()) {
                FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), EnumGuiId.BASIC_LOOT_BAG.ordinal(), worldIn, 0, 0, 0);
            } else {
                if (playerIn.isCreative()) {
                    stack = stack.copy();
                }
                getLoot(worldIn, playerIn, stack);

                stack.shrink(1);
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    public void getLoot(World worldIn, EntityPlayer playerIn, ItemStack stack) {
        ArrayList<ILootTableItem> tableItems = new ArrayList<ILootTableItem>();
        readItems(stack, tableItems);
        LootTablePool pool = new LootTablePool(null, null, null, tableItems);

        NonNullList<ItemStack> items = NonNullList.<ItemStack>create();
        pool.getLoot(items, playerIn.getRNG());

        for (ItemStack lootStack : items) {
            if (!lootStack.isEmpty()) {
                if (!playerIn.inventory.addItemStackToInventory(lootStack.copy())) {
                    UtilItems.spawnItemAtEntity(playerIn, lootStack.copy(), true);
                }
            }
        }
    }

    private void readItems(ItemStack stack, ArrayList<ILootTableItem> tableItems) {
        resetItems(tableItems);
        ContainerBasicLootBag.readItems(stack, tableItems);
    }

    private void resetItems(ArrayList<ILootTableItem> tableItems) {
        for (int i = 0; i < ContainerBasicLootBag.BAG_SIZE; i++) {
            tableItems.add(i, new LootTableItem(ItemStack.EMPTY, 0));
        }
    }

    private static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackSerialize()).registerTypeAdapter(ILootTableItem.class, new LootTableItemSerializer()).create();
    }
}
