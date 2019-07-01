package moe.plushie.rpgeconomy.bank.common.serialize;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.bank.common.Bank;

public final class BankSerializer {

    private BankSerializer() {
    }
    
    public static JsonElement serializeJson(IBank bank, boolean compact) {
        return null;
    }
    
    public static Bank deserializeJson(JsonElement json, String identifier) {
        return null;
    }
}
