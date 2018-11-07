package moe.plushie.rpg_economy.common.mail.serialize;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.rpg_economy.RPG_Economy;
import moe.plushie.rpg_economy.common.mail.MailMessage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public final class MailMessageSerializer {
    
    private MailMessageSerializer() {
    }
    
    public static JsonElement serialize(MailMessage mailMessage) {
        if (mailMessage == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender", mailMessage.getUsernameSender());
        jsonObject.addProperty("receiver", mailMessage.getUsernameReceiver());
        jsonObject.addProperty("messageText", mailMessage.getMessageText());
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            JsonObject jsonAttachment = new JsonObject();
            jsonAttachment.addProperty("id", Item.getIdFromItem(itemStack.getItem()));
            jsonAttachment.addProperty("count", itemStack.getCount());
            jsonAttachment.addProperty("damage", itemStack.getItemDamage());
            if (itemStack.getItem().isDamageable() || itemStack.getItem().getShareTag()) {
                if (itemStack.hasTagCompound()) {
                    jsonAttachment.addProperty("nbt", itemStack.getTagCompound().toString());
                }
            }
            jsonArray.add(jsonAttachment);
        }
        jsonObject.add("attachments", jsonArray);
        
        return jsonObject;
    }
    
    public static MailMessage deserialize(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JsonParser parser = new JsonParser();
            return deserialize(parser.parse(jsonString));
        } catch (Exception e) {
            RPG_Economy.getLogger().error("Error parsing mail message.");
            RPG_Economy.getLogger().error(e.getLocalizedMessage());
            return null;
        }
    }
    
    public static MailMessage deserialize(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement elementSender = jsonObject.get("sender");
            JsonElement elementReceiver = jsonObject.get("receiver");
            JsonElement elementSubject = jsonObject.get("subject");
            JsonElement elementMessageText = jsonObject.get("messageText");
            JsonElement elementAttachments = jsonObject.get("attachments");
            
            String sender = elementSender.getAsString();
            String receiver = elementReceiver.getAsString();
            String subject = elementSubject.getAsString();
            String messageText = elementMessageText.getAsString();
            JsonArray jsonArray = elementAttachments.getAsJsonArray();
            
            ArrayList<ItemStack> attachments = new ArrayList<ItemStack>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonAttachment = jsonArray.get(i).getAsJsonObject();
                JsonElement elementid = jsonAttachment.get("id");
                JsonElement elementCount = jsonAttachment.get("count");
                JsonElement elementDamage = jsonAttachment.get("damage");
                ItemStack itemStack = new ItemStack(Item.getItemById(elementid.getAsInt()), elementCount.getAsInt(), elementDamage.getAsInt());
                if (jsonAttachment.has("nbt")) {
                    JsonElement elementNbt = jsonAttachment.get("nbt");
                    NBTBase nbtBase = JsonToNBT.getTagFromJson(elementNbt.getAsString());
                    itemStack.setTagCompound((NBTTagCompound) nbtBase);
                }
                attachments.add(itemStack);
            }
            return new MailMessage(sender, receiver, subject, messageText, attachments);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
