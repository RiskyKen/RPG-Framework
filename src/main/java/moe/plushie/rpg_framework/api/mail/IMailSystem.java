package moe.plushie.rpg_framework.api.mail;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;

public interface IMailSystem {

	public IIdentifier getIdentifier();
	
    public String getName();
    
    public int getCharacterLimit();
    
    public ICost getMessageCost();
    
    public ICost getAttachmentCost();
    
    public int getInboxSize();
    
    public int getMaxAttachments();
    
    public boolean isSendingEnabled();
    
    public boolean getAllowSendToSelf();
}
