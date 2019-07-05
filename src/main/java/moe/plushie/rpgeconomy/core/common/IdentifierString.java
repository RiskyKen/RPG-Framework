package moe.plushie.rpgeconomy.core.common;

public class IdentifierString {

    public final String id;
    
    public IdentifierString(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdentifierString)) {
            return false;
        }
        IdentifierString other = (IdentifierString) obj;
        if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IdentifierString [id=" + id + "]";
    }
}
