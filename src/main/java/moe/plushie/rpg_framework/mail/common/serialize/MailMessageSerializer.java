package moe.plushie.rpg_framework.mail.common.serialize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RPGFramework;
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

    private static final String PROP_ID = "id";
    private static final String PROP_MAIL_SYSTEM = "mailSystem";
    private static final String PROP_SENDER = "sender";
    private static final String PROP_RECEIVER = "receiver";
    private static final String PROP_SEND_DATE_TIME = "sendDateTime";
    private static final String PROP_SUBJECT = "subject";
    private static final String PROP_MESSAGE_TEXT = "messageText";
    private static final String PROP_ATTACHMENTS = "attachments";
    private static final String PROP_READ = "read";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private MailMessageSerializer() {
    }

    public static JsonElement serializeJson(MailMessage mailMessage, boolean compact) {
        if (mailMessage == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_ID, mailMessage.getId());
        jsonObject.add(PROP_MAIL_SYSTEM, IdentifierSerialize.serializeJson(mailMessage.getMailSystem().getIdentifier()));
        jsonObject.addProperty(PROP_SENDER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getSender()).toString());
        if (mailMessage.getReceiver() != null) {
            jsonObject.addProperty(PROP_RECEIVER, NBTUtil.writeGameProfile(new NBTTagCompound(), mailMessage.getReceiver()).toString());
        }
        jsonObject.addProperty(PROP_SEND_DATE_TIME, SDF.format(mailMessage.getSendDateTime()));
        jsonObject.addProperty(PROP_SUBJECT, mailMessage.getSubject());
        jsonObject.addProperty(PROP_MESSAGE_TEXT, mailMessage.getMessageText());

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < mailMessage.getAttachments().size(); i++) {
            ItemStack itemStack = mailMessage.getAttachments().get(i);
            jsonArray.add(SerializeHelper.writeItemToJson(itemStack, compact));
        }
        jsonObject.add(PROP_ATTACHMENTS, jsonArray);
        jsonObject.addProperty(PROP_READ, mailMessage.isRead());
        return jsonObject;
    }

    public static MailMessage deserializeJson(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            int id = -1;
            MailSystem mailSystem = null;
            GameProfile sender = null;
            GameProfile receiver = null;
            Date sendDateTime = null;
            String subject = "";
            String messageText = "";
            NonNullList<ItemStack> attachments = NonNullList.<ItemStack>create();
            boolean read = false;

            if (jsonObject.has(PROP_ID)) {
                id = jsonObject.get(PROP_ID).getAsInt();
            }
            if (jsonObject.has(PROP_MAIL_SYSTEM)) {
                mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(IdentifierSerialize.deserializeJson(jsonObject.get(PROP_MAIL_SYSTEM)));
            }
            if (jsonObject.has(PROP_SENDER)) {
                sender = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(jsonObject.get(PROP_SENDER).getAsString()));
            }
            if (jsonObject.has(PROP_RECEIVER)) {
                receiver = NBTUtil.readGameProfileFromNBT(JsonToNBT.getTagFromJson(jsonObject.get(PROP_RECEIVER).getAsString()));
            }
            if (jsonObject.has(PROP_SEND_DATE_TIME)) {
                sendDateTime = SDF.parse(jsonObject.get(PROP_SEND_DATE_TIME).getAsString());
            }
            if (jsonObject.has(PROP_SUBJECT)) {
                subject = jsonObject.get(PROP_SUBJECT).getAsString();
            }
            if (jsonObject.has(PROP_MESSAGE_TEXT)) {
                messageText = jsonObject.get(PROP_MESSAGE_TEXT).getAsString();
            }
            if (jsonObject.has(PROP_ATTACHMENTS)) {
                JsonArray jsonArray = jsonObject.get(PROP_ATTACHMENTS).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonAttachment = jsonArray.get(i).getAsJsonObject();
                    attachments.add(SerializeHelper.readItemFromJson(jsonAttachment));
                }

            }
            if (jsonObject.has(PROP_READ)) {
                read = jsonObject.get(PROP_READ).getAsBoolean();
            }

            return new MailMessage(id, mailSystem, sender, receiver, sendDateTime, subject, messageText, attachments, read);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
