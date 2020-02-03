package moe.plushie.rpg_framework.bank.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.core.IIdentifier;
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
    private static final String PROP_TAB_ICON_INDEX = "tabIconIndex";
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
        jsonObject.addProperty(PROP_TAB_ICON_INDEX, bank.getTabIconIndex());
        JsonArray arrayCosts = new JsonArray();
        for (int i = 0; i < bank.getTabUnlockableCount(); i++) {
            arrayCosts.add(CostSerializer.serializeJson(bank.getTabUnlockCost(i), compact));
        }
        jsonObject.add(PROP_TAB_UNLOCK_COSTS, arrayCosts);
        return jsonObject;
    }

    public static Bank deserializeJson(JsonElement json, IIdentifier identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            Bank bank = new Bank(identifier);

            if (jsonObject.has(PROP_NAME)) {
                bank.setName(jsonObject.get(PROP_NAME).getAsString());
            }
            if (jsonObject.has(PROP_DEPOSIT_COST)) {
                bank.setDepositCost(CostSerializer.deserializeJson(jsonObject.get(PROP_DEPOSIT_COST)));
            }
            if (jsonObject.has(PROP_WITHDRAW_COST)) {
                bank.setWithdrawCost(CostSerializer.deserializeJson(jsonObject.get(PROP_WITHDRAW_COST)));
            }
            if (jsonObject.has(PROP_TAB_SLOT_COUNT_WIDTH)) {
                bank.setTabSlotCountWidth(jsonObject.get(PROP_TAB_SLOT_COUNT_WIDTH).getAsInt());
            }
            if (jsonObject.has(PROP_TAB_SLOT_COUNT_HEIGHT)) {
                bank.setTabSlotCountHeight(jsonObject.get(PROP_TAB_SLOT_COUNT_HEIGHT).getAsInt());
            }
            if (jsonObject.has(PROP_TAB_STARTING_COUNT)) {
                bank.setTabStartingCount(jsonObject.get(PROP_TAB_STARTING_COUNT).getAsInt());
            }
            if (jsonObject.has(PROP_TAB_MAX_COUNT)) {
                bank.setTabMaxCount(jsonObject.get(PROP_TAB_MAX_COUNT).getAsInt());
            }
            if (jsonObject.has(PROP_TAB_ICON_INDEX)) {
                bank.setTabIconIndex(jsonObject.get(PROP_TAB_ICON_INDEX).getAsInt());
            }
            if (jsonObject.has(PROP_TAB_UNLOCK_COSTS)) {
                JsonArray arrayCosts = jsonObject.get(PROP_TAB_UNLOCK_COSTS).getAsJsonArray();
                ICost[] tabUnlockCosts = new ICost[arrayCosts.size()];
                for (int i = 0; i < arrayCosts.size(); i++) {
                    tabUnlockCosts[i] = CostSerializer.deserializeJson(arrayCosts.get(i));
                }
                bank.setTabUnlockCosts(tabUnlockCosts);
            }

            return bank;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
