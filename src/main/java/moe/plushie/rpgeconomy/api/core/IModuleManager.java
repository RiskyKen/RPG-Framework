package moe.plushie.rpgeconomy.api.core;

public interface IModuleManager<T> {
    
    public String getName();
    
    public String getIdentifier();

    public void reload();
    
    public T get(String identifier);

    public T getAll();
    
    public String[] getIdentifiers();
    
    public String[] getNames();
}
