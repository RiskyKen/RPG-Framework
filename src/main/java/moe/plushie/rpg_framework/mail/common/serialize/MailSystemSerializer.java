package moe.plushie.rpg_framework.mail.common.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IGuiIcon;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.core.common.serialize.GuiIconSerialize;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import moe.plushie.rpg_framework.mail.common.MailSystem;

public class MailSystemSerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_CHARACTER_LIMIT = "character_limit";
    private static final String PROP_MESSAGE_COST = "message_cost";
    private static final String PROP_ATTACHMENT_COST = "attachment_cost";
    private static final String PROP_INBOX_SIZE = "inbox_size";
    private static final String PROP_MAX_ATTACHMENTS = "max_attachments";
    private static final String PROP_SENDING_ENABLED = "sending_enabled";
    private static final String PROP_ALLOW_SENDING_TO_SELF = "allow_sending_to_self";

    private static final String PROP_MAILBOX_FLAG_RENDER = "mailbox_flag_render";
    private static final String PROP_MAILBOX_FLAG_RENDER_DISTANCE = "mailbox_flag_render_distance";

    private static final String PROP_CHAT_NOTIFICATION_AT_LOGIN = "chat_notification_at_login";
    private static final String PROP_CHAT_NOTIFICATION_ON_NEW_MESSAGE = "chat_notification_on_new_message";

    private static final String PROP_TOAST_NOTIFICATION_AT_LOGIN = "toast_notification_at_login";
    private static final String PROP_TOAST_NOTIFICATION_ON_NEW_MESSAGE = "toast_notification_on_new_message";

    private static final String PROP_COSTALGORITHM = "cost_algorithm";

    private static final String PROP_GUI_ICONS = "gui_icons";

    private MailSystemSerializer() {
    }

    public static JsonElement serializeJson(MailSystem mailSystem) {
        if (mailSystem == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PROP_NAME, mailSystem.getName());
        jsonObject.addProperty(PROP_CHARACTER_LIMIT, mailSystem.getCharacterLimit());
        jsonObject.add(PROP_MESSAGE_COST, CostSerializer.serializeJson(mailSystem.getMessageCost(), false));
        jsonObject.add(PROP_ATTACHMENT_COST, CostSerializer.serializeJson(mailSystem.getAttachmentCost(), false));
        jsonObject.addProperty(PROP_INBOX_SIZE, mailSystem.getInboxSize());
        jsonObject.addProperty(PROP_MAX_ATTACHMENTS, mailSystem.getMaxAttachments());
        jsonObject.addProperty(PROP_SENDING_ENABLED, mailSystem.isSendingEnabled());
        jsonObject.addProperty(PROP_ALLOW_SENDING_TO_SELF, mailSystem.isAllowSendingToSelf());
        jsonObject.addProperty(PROP_MAILBOX_FLAG_RENDER, mailSystem.isMailboxFlagRender());
        jsonObject.addProperty(PROP_MAILBOX_FLAG_RENDER_DISTANCE, mailSystem.getMailboxFlagRenderDistance());
        jsonObject.addProperty(PROP_CHAT_NOTIFICATION_AT_LOGIN, mailSystem.isChatNotificationAtLogin());
        jsonObject.addProperty(PROP_CHAT_NOTIFICATION_ON_NEW_MESSAGE, mailSystem.isChatNotificationOnNewMessage());
        jsonObject.addProperty(PROP_TOAST_NOTIFICATION_AT_LOGIN, mailSystem.isToastNotificationAtLogin());
        jsonObject.addProperty(PROP_TOAST_NOTIFICATION_ON_NEW_MESSAGE, mailSystem.isToastNotificationOnNewMessage());
        jsonObject.addProperty(PROP_COSTALGORITHM, mailSystem.getCostAlgorithm());
        JsonArray jsonArray = new JsonArray();
        for (IGuiIcon guiIcon : mailSystem.getGuiIcons()) {
            JsonElement jsonElement = GuiIconSerialize.serializeJson(guiIcon);
            if (jsonElement != null) {
                jsonArray.add(jsonElement);
            }
        }
        jsonObject.add(PROP_GUI_ICONS, jsonArray);
        return jsonObject;
    }

    public static MailSystem deserializeJson(JsonElement json, IIdentifier identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = String.valueOf(identifier.getValue());
            if (jsonObject.has(PROP_NAME)) {
                name = jsonObject.get(PROP_NAME).getAsString();
            }

            MailSystem mailSystem = new MailSystem(identifier, name);

            if (jsonObject.has(PROP_CHARACTER_LIMIT)) {
                mailSystem.setCharacterLimit(jsonObject.get(PROP_CHARACTER_LIMIT).getAsInt());
            }
            if (jsonObject.has(PROP_MESSAGE_COST)) {
                mailSystem.setMessageCost(CostSerializer.deserializeJson(jsonObject.get(PROP_MESSAGE_COST)));
            }
            if (jsonObject.has(PROP_ATTACHMENT_COST)) {
                mailSystem.setAttachmentCost(CostSerializer.deserializeJson(jsonObject.get(PROP_ATTACHMENT_COST)));
            }
            if (jsonObject.has(PROP_INBOX_SIZE)) {
                mailSystem.setInboxSize(jsonObject.get(PROP_INBOX_SIZE).getAsInt());
            }
            if (jsonObject.has(PROP_MAX_ATTACHMENTS)) {
                mailSystem.setMaxAttachments(jsonObject.get(PROP_MAX_ATTACHMENTS).getAsInt());
            }
            if (jsonObject.has(PROP_SENDING_ENABLED)) {
                mailSystem.setSendingEnabled(jsonObject.get(PROP_SENDING_ENABLED).getAsBoolean());
            }
            if (jsonObject.has(PROP_ALLOW_SENDING_TO_SELF)) {
                mailSystem.setAllowSendToSelf(jsonObject.get(PROP_ALLOW_SENDING_TO_SELF).getAsBoolean());
            }
            if (jsonObject.has(PROP_MAILBOX_FLAG_RENDER)) {
                mailSystem.setMailboxFlagRender(jsonObject.get(PROP_MAILBOX_FLAG_RENDER).getAsBoolean());
            }
            if (jsonObject.has(PROP_MAILBOX_FLAG_RENDER_DISTANCE)) {
                mailSystem.setMailboxFlagRenderDistance(jsonObject.get(PROP_MAILBOX_FLAG_RENDER_DISTANCE).getAsInt());
            }
            if (jsonObject.has(PROP_CHAT_NOTIFICATION_AT_LOGIN)) {
                mailSystem.setChatNotificationAtLogin(jsonObject.get(PROP_CHAT_NOTIFICATION_AT_LOGIN).getAsBoolean());
            }
            if (jsonObject.has(PROP_CHAT_NOTIFICATION_ON_NEW_MESSAGE)) {
                mailSystem.setChatNotificationOnNewMessage(jsonObject.get(PROP_CHAT_NOTIFICATION_ON_NEW_MESSAGE).getAsBoolean());
            }
            if (jsonObject.has(PROP_TOAST_NOTIFICATION_AT_LOGIN)) {
                mailSystem.setToastNotificationAtLogin(jsonObject.get(PROP_TOAST_NOTIFICATION_AT_LOGIN).getAsBoolean());
            }
            if (jsonObject.has(PROP_TOAST_NOTIFICATION_ON_NEW_MESSAGE)) {
                mailSystem.setToastNotificationOnNewMessage(jsonObject.get(PROP_TOAST_NOTIFICATION_ON_NEW_MESSAGE).getAsBoolean());
            }
            if (jsonObject.has(PROP_COSTALGORITHM)) {
                mailSystem.setCostAlgorithm(jsonObject.get(PROP_COSTALGORITHM).getAsString());
            }
            if (jsonObject.has(PROP_GUI_ICONS)) {
                JsonArray jsonArray = jsonObject.get(PROP_GUI_ICONS).getAsJsonArray();
                IGuiIcon[] guiIcons = new IGuiIcon[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++) {
                    guiIcons[i] = GuiIconSerialize.deserializeJson(jsonArray.get(i));
                }
                mailSystem.setGuiIcons(guiIcons);
            }

            return mailSystem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
