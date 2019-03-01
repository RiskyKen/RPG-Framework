package moe.plushie.rpgeconomy.mail.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.mail.IMailSystemManager;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncMailSystems;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.mail.common.serialize.MailSystemSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MailSystemManager implements IMailSystemManager {

    private static final String DIRECTORY_NAME = "mail";

    private final File currencyDirectory;
    private final HashMap<String, MailSystem> mailSystemMap;

    public MailSystemManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        mailSystemMap = new HashMap<String, MailSystem>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void reload(boolean syncWithClients) {
        RpgEconomy.getLogger().info("Loading Mail Systems");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
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
        }
    }

    private void syncToClient(EntityPlayerMP entityPlayer) {
        RpgEconomy.getLogger().info("Sending " + mailSystemMap.size() + " mail system(s) to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }

    private void syncToAll() {
        RpgEconomy.getLogger().info("Sending " + mailSystemMap.size() + " mail system(s) to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }

    private IMessage getSyncMessage() {
        return new MessageServerSyncMailSystems(getMailSystems());
    }

    public void gotMailSystemsFromServer(MailSystem[] mailSystems) {
        RpgEconomy.getLogger().info("Got " + mailSystems.length + " mail systems(s) from server.");
        mailSystemMap.clear();
        for (MailSystem mailSystem : mailSystems) {
            mailSystemMap.put(mailSystem.getName(), mailSystem);
        }
    }

    private void loadMailSystem(File mailSystemFile) {
        RpgEconomy.getLogger().info("Loading mail system: " + mailSystemFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(mailSystemFile);
        if (jsonElement != null) {
            MailSystem mailSystem = MailSystemSerializer.deserializeJson(jsonElement);
            if (mailSystem != null) {
                mailSystemMap.put(mailSystem.getName(), mailSystem);
            }
        }
    }

    @Override
    public MailSystem getMailSystem(String name) {
        return mailSystemMap.get(name);
    }

    @Override
    public MailSystem[] getMailSystems() {
        return mailSystemMap.values().toArray(new MailSystem[mailSystemMap.size()]);
    }
    
    @Override
    public String[] getMailSystemNames() {
        return mailSystemMap.keySet().toArray(new String[mailSystemMap.size()]);
    }

    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getName());
        if (mailSystem != null) {
            mailSystem.onClientSendMailMessage(entityPlayer, mailMessage);
        }
    }
}
