package moe.plushie.rpg_framework.core.common;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public class IdentifierInt implements IIdentifier<Integer> {

    public final int id;

    public IdentifierInt(int id) {
        this.id = id;
    }
    
    @Override
    public Integer getValue() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdentifierInt)) {
            return false;
        }
        IdentifierInt other = (IdentifierInt) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IdentifierInt [id=" + id + "]";
    }
}
