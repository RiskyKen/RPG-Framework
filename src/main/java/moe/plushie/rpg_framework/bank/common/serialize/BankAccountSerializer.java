package moe.plushie.rpg_framework.bank.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.bank.common.BankAccount;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.DBPlayer;
import moe.plushie.rpg_framework.core.database.TableBankAccounts;
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
                if (i < account.getBank().getTabMaxCount()) {
                    account.unlockTab();
                    IInventory inventory = account.getTab(i);
                    for (int j = 0; j < inventory.getSizeInventory(); j++) {
                        if (j < itemsArray.size()) {
                            inventory.setInventorySlotContents(j, SerializeHelper.readItemFromJson(itemsArray.get(j)));
                        }
                    }
                }
            }
            return account;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void serializeDatabase(DBPlayer dbPlayer, IBankAccount account) {
        String tabs = serializeJson(account, true).toString();
        if (!TableBankAccounts.isAccountInDatabase(dbPlayer, account.getBank().getIdentifier())) {
            TableBankAccounts.setAccount(dbPlayer, account.getBank().getIdentifier(), tabs);
        } else {
            TableBankAccounts.updateAccount(dbPlayer, account.getBank().getIdentifier(), tabs);
        }
    }

    public static BankAccount deserializeDatabase(DBPlayer dbPlayer, IBank bank) {
        if (TableBankAccounts.isAccountInDatabase(dbPlayer, bank.getIdentifier())) {
            String tabs = TableBankAccounts.getAccountTabs(dbPlayer, bank.getIdentifier());
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
