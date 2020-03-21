package moe.plushie.rpg_framework.core.common.database;

public class DBPlayer {
    
    public static final DBPlayer MISSING = new DBPlayer(-1);
    
    private final int id;
    
    public DBPlayer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public boolean isMissing() {
        if (id < 0) {
            return true;
        }
        if (this == MISSING) {
            return true;
        }
        return false;
    }
}
