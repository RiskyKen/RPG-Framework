package moe.plushie.rpgeconomy.api.mail;

public interface IMailSystemManager {
    
    public IMailSystem getMailSystem(String identifier);
    
    public IMailSystem[] getMailSystems();
    
    public String[] getMailSystemNames();
}
