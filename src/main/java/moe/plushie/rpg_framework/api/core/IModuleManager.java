package moe.plushie.rpg_framework.api.core;

public interface IModuleManager<T> {
    
    public String getName();
    
    public IIdentifier getIdentifier();
    
    public T get(IIdentifier identifier);

    public T getAll();
    
    public IIdentifier[] getIdentifiers();
    
    public String[] getNames();
}
