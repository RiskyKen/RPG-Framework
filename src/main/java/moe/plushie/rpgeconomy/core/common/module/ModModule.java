package moe.plushie.rpgeconomy.core.common.module;

import java.util.ArrayList;

public abstract class ModModule implements IModModule {

    public static final ArrayList<IModModule> MOD_MODULES = new ArrayList<IModModule>();

    private final String name;
    private final ArrayList<IModModule> dependencies;

    public ModModule(String name) {
        this.name = name;
        this.dependencies = new ArrayList<IModModule>();
        MOD_MODULES.add(this);
    }
    
    protected void addDependency(IModModule modModule) {
        dependencies.add(modModule);
    }

    @Override
    public String getName() {
        return name;
    }
}
