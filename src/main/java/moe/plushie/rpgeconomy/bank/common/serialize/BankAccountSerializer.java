package moe.plushie.rpgeconomy.bank.common.serialize;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.bank.common.BankAccount;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.core.database.Database;
import net.minecraft.entity.player.EntityPlayer;

public final class BankAccountSerializer {
    
    private BankAccountSerializer() {
    }
    
    public static JsonElement serializeJson(IBankAccount bankAccount, boolean saveItems) {
        return null;
    }
    
    public static BankAccount deserializeJson(JsonElement json, IBank bank) {
        return null;
    }

    public static void serializeDatabase(EntityPlayer player, IBankAccount account) {
        Database.BANKS_TABLE.create();
        String tabs = serializeJson(account, true).toString();
        if (account.getDatabaseId() == -1) {
            int id = Database.BANKS_TABLE.setAccount(player, account.getBank().getIdentifier(), tabs);
            account.setDatabaseId(id);
        } else {
            Database.BANKS_TABLE.updateAccount(account.getDatabaseId(), tabs);
        }
    }

    public static BankAccount deserializeDatabase(EntityPlayer player, IBank bank) {
        Database.BANKS_TABLE.create();
        String tabs = Database.BANKS_TABLE.getAccountTabs(player, bank.getIdentifier());
        JsonElement tabsJson = SerializeHelper.stringToJson(tabs);
        deserializeJson(tabsJson, bank);
        return deserializeJson(tabsJson, bank);
    }
}
