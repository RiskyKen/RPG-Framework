package moe.plushie.rpgeconomy.api.mail;

public interface IMailSystemManager {
    
    public IMailSystem getMailSystem(String name);
    
    public IMailSystem[] getMailSystems();
    
    public String[] getMailSystemNames();
}
