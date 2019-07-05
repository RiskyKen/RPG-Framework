package moe.plushie.rpgeconomy.core.common;

public class IdentifierInt {
    
    public final int id;
    
    public IdentifierInt(int id) {
        this.id = id;
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
