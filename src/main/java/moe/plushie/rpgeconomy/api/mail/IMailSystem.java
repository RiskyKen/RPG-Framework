package moe.plushie.rpgeconomy.api.mail;

import moe.plushie.rpgeconomy.api.currency.IWallet;

public interface IMailSystem {

	public String getIdentifier();
	
    public String getName();
    
    public int getCharacterLimit();
    
    public IWallet getMessageCost();
    
    public IWallet getAttachmentCost();
    
    public int getInboxSize();
    
    public int getMaxAttachments();
}
