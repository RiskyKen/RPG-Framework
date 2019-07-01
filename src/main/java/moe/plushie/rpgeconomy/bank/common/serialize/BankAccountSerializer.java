package moe.plushie.rpgeconomy.bank.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.bank.common.BankAccount;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.core.database.Database;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public final class BankAccountSerializer {

    private BankAccountSerializer() {
    }

    public static JsonElement serializeJson(IBankAccount account, boolean saveItems) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < account.getTabCount(); i++) {
            JsonArray itemsArray = new JsonArray();
            IInventory inventory = account.getTab(i);
            for (int j = 0; j < inventory.getSizeInventory(); j++) {
                itemsArray.add(SerializeHelper.writeItemToJson(inventory.getStackInSlot(j), false));
            }
            jsonArray.add(itemsArray);
        }
        return jsonArray;
    }

    public static BankAccount deserializeJson(JsonElement json, IBank bank) {
        try {
            BankAccount account = new BankAccount(bank);
            JsonArray jsonArray = (JsonArray) json;
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonArray itemsArray = jsonArray.get(i).getAsJsonArray();
                account.unlockTab();
                IInventory inventory = account.getTab(i);
                for (int j = 0; j < inventory.getSizeInventory(); j++) {
                    if (j < inventory.getSizeInventory()) {
                        inventory.setInventorySlotContents(j, SerializeHelper.readItemFromJson(itemsArray.get(j)));
                    }
                }
            }
            return account;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void serializeDatabase(EntityPlayer player, IBankAccount account) {
        Database.BANKS_TABLE.create();
        String tabs = serializeJson(account, true).toString();
        if (!Database.BANKS_TABLE.isAccountInDatabase(player, account.getBank().getIdentifier())) {
            Database.BANKS_TABLE.setAccount(player, account.getBank().getIdentifier(), tabs);
        } else {
            Database.BANKS_TABLE.updateAccount(player, account.getBank().getIdentifier(), tabs);
        }
    }

    public static BankAccount deserializeDatabase(EntityPlayer player, IBank bank) {
        Database.BANKS_TABLE.create();
        if (Database.BANKS_TABLE.isAccountInDatabase(player, bank.getIdentifier())) {
            String tabs = Database.BANKS_TABLE.getAccountTabs(player, bank.getIdentifier());
            JsonElement tabsJson = SerializeHelper.stringToJson(tabs);
            deserializeJson(tabsJson, bank);
            return deserializeJson(tabsJson, bank);
        } else {
            BankAccount account = new BankAccount(bank);
            account.setNewAccount();
            return account;
        }
    }
}
