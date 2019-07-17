package moe.plushie.rpg_framework.bank.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.bank.common.Bank;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;

public final class BankSerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_DEPOSIT_COST = "depositCost";
    private static final String PROP_WITHDRAW_COST = "withdrawCost";
    private static final String PROP_TAB_SLOT_COUNT_WIDTH = "tabSlotCountWidth";
    private static final String PROP_TAB_SLOT_COUNT_HEIGHT = "tabSlotCountHeight";
    private static final String PROP_TAB_STARTING_COUNT = "tabStartingCount";
    private static final String PROP_TAB_MAX_COUNT = "tabMaxCount";
    private static final String PROP_TAB_UNLOCK_COSTS = "tabUnlockCosts";
    
    private BankSerializer() {
    }
    
    public static JsonElement serializeJson(IBank bank, boolean compact) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_NAME, bank.getName());
        jsonObject.add(PROP_DEPOSIT_COST, CostSerializer.serializeJson(bank.getDepositCost(), compact));
        jsonObject.add(PROP_WITHDRAW_COST, CostSerializer.serializeJson(bank.getWithdrawCost(), compact));
        jsonObject.addProperty(PROP_TAB_SLOT_COUNT_WIDTH, bank.getTabSlotCountWidth());
        jsonObject.addProperty(PROP_TAB_SLOT_COUNT_HEIGHT, bank.getTabSlotCountHeight());
        jsonObject.addProperty(PROP_TAB_STARTING_COUNT, bank.getTabStartingCount());
        jsonObject.addProperty(PROP_TAB_MAX_COUNT, bank.getTabMaxCount());
        JsonArray arrayCosts = new JsonArray();
        for (int i = 0; i < bank.getTabUnlockableCount(); i++) {
            arrayCosts.add(CostSerializer.serializeJson(bank.getTabUnlockCost(i), compact));
        }
        jsonObject.add(PROP_TAB_UNLOCK_COSTS, arrayCosts);
        return jsonObject;
    }
    
    public static Bank deserializeJson(JsonElement json, String identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.get(PROP_NAME).getAsString();
            ICost depositCost = CostSerializer.deserializeJson(jsonObject.get(PROP_DEPOSIT_COST));
            ICost withdrawCost = CostSerializer.deserializeJson(jsonObject.get(PROP_WITHDRAW_COST));
            int tabSlotCountWidth = jsonObject.get(PROP_TAB_SLOT_COUNT_WIDTH).getAsInt();
            int tabSlotCountHeight = jsonObject.get(PROP_TAB_SLOT_COUNT_HEIGHT).getAsInt();
            int tabStartingCount = jsonObject.get(PROP_TAB_STARTING_COUNT).getAsInt();
            int tabMaxCount = jsonObject.get(PROP_TAB_MAX_COUNT).getAsInt();
            JsonArray arrayCosts = jsonObject.get(PROP_TAB_UNLOCK_COSTS).getAsJsonArray();
            ICost[] tabUnlockCosts = new ICost[arrayCosts.size()];
            for (int i = 0; i < arrayCosts.size(); i++) {
                tabUnlockCosts[i] = CostSerializer.deserializeJson(arrayCosts.get(i));
            }
            return new Bank(identifier, name, depositCost, withdrawCost, tabSlotCountWidth, tabSlotCountHeight, tabStartingCount, tabMaxCount, tabUnlockCosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
