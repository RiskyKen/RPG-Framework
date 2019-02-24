package moe.plushie.rpgeconomy.mail.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.mail.common.serialize.MailSystemSerializer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MailSystemManager {

    private static final String DIRECTORY_NAME = "mail";
    
    private final File currencyDirectory;
    private final HashMap<String, MailSystem> mailSystemMap;

    public MailSystemManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        mailSystemMap = new HashMap<String, MailSystem>();
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
    
    public MailSystem getMailSystem(String name) {
        return mailSystemMap.get(name);
    }

    public MailSystem[] getMailSystems() {
        return mailSystemMap.values().toArray(new MailSystem[mailSystemMap.size()]);
    }

    public void onClientSendMailMessage(EntityPlayerMP entityPlayer, MailMessage mailMessage) {
        MailSystem mailSystem = getMailSystem(mailMessage.getMailSystem().getName());
        if (mailSystem != null) {
            mailSystem.onClientSendMailMessage(entityPlayer, mailMessage);
        }
    }
}
