package moe.plushie.rpg_framework.api.mail;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IMailSystemManager {
    
    public IMailSystem getMailSystem(IIdentifier identifier);
    
    public IMailSystem[] getMailSystems();
    
    public String[] getMailSystemNames();
}
