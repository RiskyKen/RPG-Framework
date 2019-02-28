package moe.plushie.rpgeconomy.mail.common.serialize;

import java.util.Calendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.mail.common.MailMessage;
import moe.plushie.rpgeconomy.mail.common.MailSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;

public final class MailMessageSerializer {
    
    private static final String PROP_MAIL_SYSTEM = "mailSystem";
    private static final String PROP_SENDER = "sender";
    private static final String PROP_RECEIVER = "receiver";
    private static final String PROP_SEND_DATE_TIME = "sendDateTime";
    private static final String PROP_SUBJECT = "subject";
    private static final String PROP_MESSAGE_TEXT = "messageText";
    private static final String PROP_ATTACHMENTS = "attachments";
    
    private MailMessageSerializer() {
    }
    
    public static JsonElement serialize(MailMessage mailMessage) {
        if (mailMessage == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty(PROP_MAIL_SYSTEM, mailMessage.getMailSystem().getName());
        jsonObject.addProperty(PROP_SENDER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getSender()).toString());
        jsonObject.addProperty(PROP_RECEIVER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getReceiver()).toString());
        jsonObject.addProperty(PROP_SEND_DATE_TIME, mailMessage.getSendDateTime().toString());
        jsonObject.addProperty(PROP_SUBJECT, mailMessage.getSubject());
        jsonObject.addProperty(PROP_MESSAGE_TEXT, mailMessage.getMessageText());
        
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            jsonArray.add(SerializeHelper.writeItemToJson(itemStack));
        }
        jsonObject.add(PROP_ATTACHMENTS, jsonArray);
        
        return jsonObject;
    }
    
    public static MailMessage deserialize(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            
            JsonElement elementMailSystem = jsonObject.get(PROP_MAIL_SYSTEM);
            JsonElement elementSender = jsonObject.get(PROP_SENDER);
            JsonElement elementReceiver = jsonObject.get(PROP_RECEIVER);
            JsonElement elementSendDateTime = jsonObject.get(PROP_SEND_DATE_TIME);
            JsonElement elementSubject = jsonObject.get(PROP_SUBJECT);
            JsonElement elementMessageText = jsonObject.get(PROP_MESSAGE_TEXT);
            JsonElement elementAttachments = jsonObject.get(PROP_ATTACHMENTS);
            
            MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(elementMailSystem.getAsString());
            GameProfile sender = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(elementSender.getAsString()));
            GameProfile receiver = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(elementReceiver.getAsString()));
            Calendar sendDateTime = Calendar.getInstance(); // TODO Load date from string.
            String subject = elementSubject.getAsString();
            String messageText = elementMessageText.getAsString();
            
            JsonArray jsonArray = elementAttachments.getAsJsonArray();
            NonNullList<ItemStack> attachments = NonNullList.<ItemStack>withSize(0, ItemStack.EMPTY);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonAttachment = jsonArray.get(i).getAsJsonObject();
                attachments.add(SerializeHelper.readItemFromJson(jsonAttachment));
            }
            
            return new MailMessage(mailSystem, sender, receiver, sendDateTime, subject, messageText, attachments);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
