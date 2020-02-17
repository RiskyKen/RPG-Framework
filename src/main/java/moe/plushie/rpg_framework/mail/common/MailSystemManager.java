package moe.plushie.rpg_framework.mail.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystemManager;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncMailSystems;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.mail.common.serialize.MailSystemSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MailSystemManager implements IMailSystemManager {

    private static final String DIRECTORY_NAME = "mail";

    private final File mailDirectory;
    private final HashMap<IIdentifier, MailSystem> mailSystemMap;
    private final MailNotificationManager notificationManager;

    public MailSystemManager(File modDirectory) {
        mailDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!mailDirectory.exists()) {
            mailDirectory.mkdir();
        }
        mailSystemMap = new HashMap<IIdentifier, MailSystem>();
        notificationManager = new MailNotificationManager();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public MailNotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void reload(boolean syncWithClients) {
        RPGFramework.getLogger().info("Loading Mail Systems");
        File[] files = mailDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        mailSystemMap.clear();
        for (File file : files) {
            loadMailSystem(file);
        }
        if (syncWithClients) {
            syncToAll();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            syncToClient((EntityPlayerMP) event.player);
            notificationManager.onPlayerLoggedIn(event);
        }
    }

    public void syncToClient(EntityPlayerMP entityPlayer) {
        RPGFramework.getLogger().info("Sending " + mailSystemMap.size() + " mail system(s) to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }

    private void syncToAll() {
        RPGFramework.getLogger().info("Sending " + mailSystemMap.size() + " mail system(s) to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }

    private IMessage getSyncMessage() {
        return new MessageServerSyncMailSystems(getMailSystems());
    }

    public void gotMailSystemsFromServer(MailSystem[] mailSystems) {
        RPGFramework.getLogger().info("Got " + mailSystems.length + " mail systems(s) from server.");
        mailSystemMap.clear();
        for (MailSystem mailSystem : mailSystems) {
            mailSystemMap.put(mailSystem.getIdentifier(), mailSystem);
        }
    }

    private void loadMailSystem(File mailSystemFile) {
        RPGFramework.getLogger().info("Loading mail system: " + mailSystemFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(mailSystemFile);
        if (jsonElement != null) {
            MailSystem mailSystem = MailSystemSerializer.deserializeJson(jsonElement, new IdentifierString(mailSystemFile.getName()));
            if (mailSystem != null) {
                mailSystemMap.put(mailSystem.getIdentifier(), mailSystem);
            }
        }
    }

    @Override
    public MailSystem getMailSystem(IIdentifier identifier) {
        return mailSystemMap.get(identifier);
    }

    @Override
    public MailSystem[] getMailSystems() {
        MailSystem[] mailSystems = mailSystemMap.values().toArray(new MailSystem[mailSystemMap.size()]);
        Arrays.sort(mailSystems);
        return mailSystems;
    }

    @Override
    public String[] getMailSystemNames() {
        return mailSystemMap.keySet().toArray(new String[mailSystemMap.size()]);
    }

    @Override
    public void onSendMailMessages(IMailSendCallback callback, GameProfile[] receivers, MailMessage mailMessage) {
        DatabaseManager.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<GameProfile> success = new ArrayList<GameProfile>();
                ArrayList<GameProfile> failed = new ArrayList<GameProfile>();
                MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getIdentifier());
                if (mailSystem != null) {
                    for (GameProfile receiver : receivers) {
                        MailMessage m = new MailMessage(mailMessage.getId(), mailMessage.getMailSystem(), mailMessage.getSender(), receiver, mailMessage.getSendDateTime(), mailMessage.getSubject(), mailMessage.getMessageText(), mailMessage.getAttachments(), mailMessage.isRead());
                        if (mailSystem.onSendMailMessage(m)) {
                            success.add(receiver);
                            continue;
                        } else {
                            failed.add(receiver);
                        }
                    }
                }
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        if (!success.isEmpty()) {
                            notifyClients(success);
                        }
                        if (callback != null) {
                            callback.onMailResult(success, failed);
                        }
                    }
                });

            }
        });
    }

    private void notifyClients(ArrayList<GameProfile> clientProfiles) {
        List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (GameProfile profile : clientProfiles) {
            for (int i = 0; i < playerEntityList.size(); i++) {
                EntityPlayerMP entityPlayerMP = playerEntityList.get(i);
                if (PlayerUtils.gameProfilesMatch(entityPlayerMP.getGameProfile(), profile)) {
                    RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayerMP, false, true);
                }
            }
        }
    }
}
