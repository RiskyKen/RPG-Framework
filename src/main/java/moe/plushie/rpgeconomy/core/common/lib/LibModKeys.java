package moe.plushie.rpgeconomy.core.common.lib;

public class LibModKeys {
    
    public static final String CATEGORY = "keys." + LibModInfo.ID + ":category";
    
    public static enum ModKey {
        OPEN_WALLET_1("open_wallet_1"),
        OPEN_WALLET_2("open_wallet_2");
        
        private final String name;
        
        private ModKey(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String getFullName() {
            return "keys." + LibModInfo.ID + "." + name;
        }
    }
}
