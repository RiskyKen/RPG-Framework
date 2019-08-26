package moe.plushie.rpg_framework.mail.common.serialize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
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

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private MailMessageSerializer() {
    }

    public static JsonElement serializeJson(MailMessage mailMessage, boolean compact) {
        if (mailMessage == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.add(PROP_MAIL_SYSTEM, IdentifierSerialize.serializeJson(mailMessage.getMailSystem().getIdentifier()));
        jsonObject.addProperty(PROP_SENDER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getSender()).toString());
        jsonObject.addProperty(PROP_RECEIVER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getReceiver()).toString());
        jsonObject.addProperty(PROP_SEND_DATE_TIME, SDF.format(mailMessage.getSendDateTime()));
        jsonObject.addProperty(PROP_SUBJECT, mailMessage.getSubject());
        jsonObject.addProperty(PROP_MESSAGE_TEXT, mailMessage.getMessageText());

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            jsonArray.add(SerializeHelper.writeItemToJson(itemStack, compact));
        }
        jsonObject.add(PROP_ATTACHMENTS, jsonArray);

        return jsonObject;
    }

    public static MailMessage deserializeJson(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement elementMailSystem = jsonObject.get(PROP_MAIL_SYSTEM);
            JsonElement elementSender = jsonObject.get(PROP_SENDER);
            JsonElement elementReceiver = jsonObject.get(PROP_RECEIVER);
            JsonElement elementSendDateTime = jsonObject.get(PROP_SEND_DATE_TIME);
            JsonElement elementSubject = jsonObject.get(PROP_SUBJECT);
            JsonElement elementMessageText = jsonObject.get(PROP_MESSAGE_TEXT);
            JsonElement elementAttachments = jsonObject.get(PROP_ATTACHMENTS);

            MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(IdentifierSerialize.deserializeJson(elementMailSystem));
            GameProfile sender = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(elementSender.getAsString()));
            GameProfile receiver = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(elementReceiver.getAsString()));
            Date sendDateTime = SDF.parse(elementSendDateTime.getAsString());
            String subject = elementSubject.getAsString();
            String messageText = elementMessageText.getAsString();

            JsonArray jsonArray = elementAttachments.getAsJsonArray();
            NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
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

    public static void serializeDatabase(MailMessage mailMessage) {

    }

    public static MailMessage deserializeDatabase(int messageId) {
        return null;
    }
}
