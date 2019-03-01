package moe.plushie.rpgeconomy.core.common.module;

import java.util.ArrayList;

public abstract class ModModule implements IModModule {

    public static final ArrayList<IModModule> MOD_MODULES = new ArrayList<IModModule>();
    
    public ModModule() {
        MOD_MODULES.add(this);
    }
}
