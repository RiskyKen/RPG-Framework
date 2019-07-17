package moe.plushie.rpg_framework.api.mail;

public interface IMailSystemManager {
    
    public IMailSystem getMailSystem(String identifier);
    
    public IMailSystem[] getMailSystems();
    
    public String[] getMailSystemNames();
}
