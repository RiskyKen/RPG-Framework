package moe.plushie.rpgeconomy.api.mail;

import java.util.Calendar;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IMailMessage {
    
    public IMailSystem getMailSystem();
    
    public GameProfile getSender();

    public GameProfile getReceiver();
    
    public Calendar getSendDateTime();

    public String getSubject();
    
    public String getMessageText();
    
    public NonNullList<ItemStack> getAttachments();
}
