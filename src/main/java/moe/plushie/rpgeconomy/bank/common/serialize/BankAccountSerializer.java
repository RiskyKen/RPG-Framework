package moe.plushie.rpgeconomy.bank.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.bank.common.BankAccount;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.core.database.TableBankAccounts;
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
        TableBankAccounts.create();
        String tabs = serializeJson(account, true).toString();
        if (!TableBankAccounts.isAccountInDatabase(player, account.getBank().getIdentifier())) {
            TableBankAccounts.setAccount(player, account.getBank().getIdentifier(), tabs);
        } else {
            TableBankAccounts.updateAccount(player, account.getBank().getIdentifier(), tabs);
        }
    }

    public static BankAccount deserializeDatabase(EntityPlayer player, IBank bank) {
        TableBankAccounts.create();
        if (TableBankAccounts.isAccountInDatabase(player, bank.getIdentifier())) {
            String tabs = TableBankAccounts.getAccountTabs(player, bank.getIdentifier());
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
