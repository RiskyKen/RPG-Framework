package moe.plushie.rpgeconomy.api.mail;

import moe.plushie.rpgeconomy.api.currency.ICost;

public interface IMailSystem {

	public String getIdentifier();
	
    public String getName();
    
    public int getCharacterLimit();
    
    public ICost getMessageCost();
    
    public ICost getAttachmentCost();
    
    public int getInboxSize();
    
    public int getMaxAttachments();
}
