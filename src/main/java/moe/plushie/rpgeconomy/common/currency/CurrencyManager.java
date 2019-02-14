package moe.plushie.rpgeconomy.common.currency;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.common.currency.serialize.CurrencySerializer;
import moe.plushie.rpgeconomy.common.utils.SerializeHelper;

public class CurrencyManager {
    
    private static final String DIRECTORY_NAME = "currency";
    
    private final File currencyDirectory;
    private final HashMap<String, Currency> currencyMap;
    
    public CurrencyManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        currencyMap = new HashMap<String, Currency>();
        reload();
    }
    
    public void reload() {
        RpgEconomy.getLogger().info("Loading Currency");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        currencyMap.clear();
        for (File file : files) {
            loadCurrency(file);
        }
    }
    
    private  void loadCurrency(File currencyFile) {
        RpgEconomy.getLogger().info("Loading currency: " + currencyFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(currencyFile);
        if (jsonElement != null) {
            Currency currency = CurrencySerializer.deserialize(jsonElement);
            if (currency != null) {
                currencyMap.put(currency.getName(), currency);
            }
        }
    }
    
    public Currency getCurrency(String name) {
        return currencyMap.get(name);
    }
}