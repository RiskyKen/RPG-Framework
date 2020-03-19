package moe.plushie.rpg_framework.core.common.lib;

public enum EnumGuiId {
    
    MANAGER(false),
    MAIL_BOX(true),
    WALLET(false),
    SHOP_TILE(true),
    SHOP_COMMAND(false),
    BANK_TILE(true),
    BANK_COMMAND(false),
    LOOT_EDITOR_COMMAND(false),
    BASIC_LOOT_BAG(false),
    STATS(false);
    
    private final boolean tile;
    
    private EnumGuiId() {
        this(true);
    }
    
    private EnumGuiId(boolean tile) {
        this.tile = tile;
    }
    
    public boolean isTile() {
        return tile;
    }
}
