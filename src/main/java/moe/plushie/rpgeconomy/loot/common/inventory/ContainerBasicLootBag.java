package moe.plushie.rpgeconomy.loot.common.inventory;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import moe.plushie.rpgeconomy.api.loot.ILootTableItem;
import moe.plushie.rpgeconomy.core.common.inventory.ModContainer;
import moe.plushie.rpgeconomy.core.common.serialize.ItemStackSerialize;
import moe.plushie.rpgeconomy.loot.common.LootTableItem;
import moe.plushie.rpgeconomy.loot.common.LootTableItemSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class ContainerBasicLootBag extends ModContainer implements IInventoryChangedListener {

    private static final String TAG_TABLE_ITEMS = "table_items";
    private static final int BAG_WIDTH = 6;
    private static final int BAG_HEIGHT = 6;
    private static final int BAG_SIZE = BAG_WIDTH * BAG_HEIGHT;

    private final EntityPlayer player;
    private final ItemStack stack;
    private ArrayList<ILootTableItem> tableItems;
    private final InventoryBasic lootInv;

    public ContainerBasicLootBag(EntityPlayer player, ItemStack stack) {
        super(player.inventory);
        this.player = player;
        this.stack = stack;
        tableItems = new ArrayList<ILootTableItem>();
        lootInv = new InventoryBasic("", false, BAG_SIZE);
        lootInv.addInventoryChangeListener(this);
        readItems();
        for (int iy = 0; iy < BAG_HEIGHT; iy++) {
            for (int ix = 0; ix < BAG_WIDTH; ix++) {
                addSlotToContainer(new Slot(lootInv, ix + iy * BAG_WIDTH, 9 + ix * 18, 24 + iy * 18));
            }
        }

        addPlayerSlots(8, 140);
    }

    private void moveToInv() {
        for (int i = 0; i < BAG_SIZE; i++) {
            lootInv.setInventorySlotContents(i, tableItems.get(i).getItem());
        }
    }

    private void moveFromInv() {
        for (int i = 0; i < BAG_SIZE; i++) {
            ILootTableItem item = tableItems.get(i);
            tableItems.add(i, new LootTableItem(lootInv.getStackInSlot(i), item.getWeight()));
        }
    }

    private void writeItems() {
        moveFromInv();
        Gson gson = getGson();
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < BAG_SIZE; i++) {
            tagList.appendTag(new NBTTagString(gson.toJsonTree(tableItems.get(i)).toString()));
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag(TAG_TABLE_ITEMS, tagList);
    }

    private void readItems() {
        Gson gson = getGson();
        resetItems();
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(TAG_TABLE_ITEMS, NBT.TAG_LIST)) {
            NBTTagList tagList = stack.getTagCompound().getTagList(TAG_TABLE_ITEMS, NBT.TAG_STRING);
            for (int i = 0; i < tagList.tagCount(); i++) {
                String data = tagList.getStringTagAt(i);
                tableItems.set(i, gson.fromJson(data, ILootTableItem.class));
            }
        }
        moveToInv();
    }

    private void resetItems() {
        for (int i = 0; i < BAG_SIZE; i++) {
            tableItems.add(i, new LootTableItem(ItemStack.EMPTY, 0));
        }
        moveToInv();
    }

    private static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackSerialize()).registerTypeAdapter(ILootTableItem.class, new LootTableItemSerializer()).create();
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        if (!player.getEntityWorld().isRemote) {
            // ItemStackHelper.saveAllItems(tag, lootInv.get, true);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (!player.getEntityWorld().isRemote) {
            writeItems();
        }
        super.onContainerClosed(playerIn);
    }
}
