package moe.plushie.rpg_framework.core.common;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public class IdentifierString implements IIdentifier<String> {

    public final String id;

    public IdentifierString(String id) {
        this.id = id;
    }

    @Override
    public String getValue() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentifierString other = (IdentifierString) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IdentifierString [id=" + id + "]";
    }
}
