package moe.plushie.rpg_framework.core.common.database;

import moe.plushie.rpg_framework.api.core.IDBPlayer;

public class DBPlayer implements IDBPlayer {

    public static final DBPlayer MISSING = new DBPlayer(-1);

    protected final int id;

    public DBPlayer(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isMissing() {
        if (id < 0) {
            return true;
        }
        if (this == MISSING) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DBPlayer [id=" + id + "]";
    }
}
