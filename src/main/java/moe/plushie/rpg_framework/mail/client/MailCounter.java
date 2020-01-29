package moe.plushie.rpg_framework.mail.client;

import java.util.HashMap;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;

public final class MailCounter {

    private static final HashMap<IIdentifier, Integer> COUNT_MAP = new HashMap<IIdentifier, Integer>();

    private MailCounter() {
    }

    public static int getUnreadMailCount(IMailSystem mailSystem) {
        synchronized (COUNT_MAP) {
            if (COUNT_MAP.containsKey(mailSystem.getIdentifier())) {
                return COUNT_MAP.get(mailSystem.getIdentifier());
            } else {
                return 0;
            }
        }
    }

    public static void setUnreadMailCount(IMailSystem mailSystem, int unreadCount) {
        RPGFramework.getLogger().info(String.format("Setting unread mail count to %d for %s.", unreadCount, mailSystem.getName()));
        synchronized (COUNT_MAP) {
            COUNT_MAP.put(mailSystem.getIdentifier(), unreadCount);
        }
    }
}
