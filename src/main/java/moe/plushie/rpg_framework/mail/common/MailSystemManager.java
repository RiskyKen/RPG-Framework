package moe.plushie.rpg_framework.mail.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.api.mail.IMailSystemManager;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncMailSystems;
import moe.plushie.rpg_framework.core.common.utils.PlayerUtils;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.serialize.MailSystemSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MailSystemManager implements IMailSystemManager {

    private static final String DIRECTORY_NAME = "mail";

    private final File mailDirectory;
    private final ConcurrentHashMap<IIdentifier, MailSystem> mailSystemMap;
    private final MailNotificationManager notificationManager;

    public MailSystemManager(File modDirectory) {
        mailDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!mailDirectory.exists()) {
            mailDirectory.mkdir();
        }
        mailSystemMap = new ConcurrentHashMap<IIdentifier, MailSystem>();
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

    public int getMailSystemIndex(IMailSystem mailSystem) {
        if (mailSystem == null) {
            return -1;
        }
        IMailSystem[] mailSystems = getMailSystems();
        for (int i = 0; i < mailSystems.length; i++) {
            if (mailSystem == mailSystems[i]) {
                return i;
            }
        }
        return -1;
    }

    public IMailSystem getMailSystem(int index) {
        IMailSystem[] mailSystems = getMailSystems();
        if (index >= 0 & index < mailSystems.length) {
            return mailSystems[index];
        }
        return null;
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
        DatabaseManager.createTaskAndExecute(new Runnable() {
            MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getIdentifier());

            @Override
            public void run() {
                ArrayList<GameProfile> success = new ArrayList<GameProfile>();
                ArrayList<GameProfile> failed = new ArrayList<GameProfile>();
                ArrayList<GameProfile> specialNames = new ArrayList<GameProfile>();
                if (mailSystem != null) {
                    for (GameProfile receiver : receivers) {
                        if (receiver.getName() != null && receiver.getName().startsWith("@")) {
                            specialNames.add(receiver);
                        } else {
                            if (mailSystem.sendMailMessage(mailMessage.updateReceiver(receiver))) {
                                success.add(receiver);
                            } else {
                                failed.add(receiver);
                            }
                        }
                    }
                }
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        for (GameProfile special : specialNames) {
                            if (sendSpecialMailMessage(special.getName(), mailMessage)) {
                                success.add(special);
                            } else {
                                failed.add(special);
                            }
                        }
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

    private boolean sendSpecialMailMessage(String specialName, MailMessage mailMessage) {
        if (specialName.equalsIgnoreCase("@a")) {
            sendToAll(mailMessage);
            return true;
        }
        if (specialName.equalsIgnoreCase("@online")) {
            sendToAllOnline(mailMessage);
            return true;
        }
        if (specialName.equalsIgnoreCase("@whitelist")) {
            sendToWhiteList(mailMessage);
            return true;
        }
        return false;
    }

    private void sendToAll(MailMessage mailMessage) {
        MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getIdentifier());
        if (mailSystem == null) {
            return;
        }
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                ArrayList<GameProfile> gameProfiles = TablePlayers.getAllPlayers();
                for (GameProfile profile : gameProfiles) {
                    if (mailSystem.sendMailMessage(mailMessage.updateReceiver(profile))) {
                        notifyClientMainThread(profile);
                    }
                }
            }
        });
    }

    private void sendToAllOnline(MailMessage mailMessage) {
        MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getIdentifier());
        if (mailSystem == null) {
            return;
        }
        List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (int i = 0; i < playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = playerEntityList.get(i);
            GameProfile profile = new GameProfile(entityPlayerMP.getGameProfile().getId(), entityPlayerMP.getGameProfile().getName());
            DatabaseManager.createTaskAndExecute(new Runnable() {
                @Override
                public void run() {
                    if (mailSystem.sendMailMessage(mailMessage.updateReceiver(profile))) {
                        notifyClientMainThread(profile);
                    }
                }
            });
        }

    }

    private void sendToWhiteList(MailMessage mailMessage) {
        MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getIdentifier());
        if (mailSystem == null) {
            return;
        }
        UserListWhitelist whiteList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getWhitelistedPlayers();
        for (String key : whiteList.getKeys()) {
            GameProfile gameProfile = whiteList.getByName(key);
            GameProfile profile = new GameProfile(gameProfile.getId(), gameProfile.getName());
            DatabaseManager.createTaskAndExecute(new Runnable() {
                @Override
                public void run() {
                    if (mailSystem.sendMailMessage(mailMessage.updateReceiver(profile))) {
                        notifyClientMainThread(profile);
                    }
                }
            });
        }
    }

    public void notifyClientMainThread(GameProfile clientProfile) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
                for (int i = 0; i < playerEntityList.size(); i++) {
                    EntityPlayerMP entityPlayerMP = playerEntityList.get(i);
                    if (PlayerUtils.gameProfilesMatch(entityPlayerMP.getGameProfile(), clientProfile)) {
                        RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(entityPlayerMP, false, true);
                        break;
                    }
                }
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

    public void saveMailSystem(MailSystem mailSystem) {
        RPGFramework.getLogger().info("Saving mail system: " + mailSystem.getIdentifier());
        JsonElement jsonData = MailSystemSerializer.serializeJson(mailSystem);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(new File(mailDirectory, String.valueOf(mailSystem.getIdentifier().getValue())), Charsets.UTF_8, gson.toJson(jsonData));
    }
}
