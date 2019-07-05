package moe.plushie.rpgeconomy.core.common.lib;

public enum EnumGuiId {
    
    MAIL_BOX(true),
    WALLET(false),
    SHOP_TILE(true),
    SHOP_COMMAND(false),
    BANK_TILE(true),
    BANK_COMMAND(false);
    
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
