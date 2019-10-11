package moe.plushie.rpg_framework.core.common.command;

import java.io.File;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.TablePlayers;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;

public class CommandIntegration extends ModSubCommands {

    public CommandIntegration(ModCommand parent) {
        super(parent, "integration");
        addSubCommand(new CommandExecute(this, "fantasticlib-import", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                File fantasticPlayersFile = new File(RPGFramework.getProxy().getConfigDirectory(), "fantasticlib/reference/players.txt");
                if (!fantasticPlayersFile.exists()) {
                    player.sendMessage(new TextComponentString("File was not found."));
                    return;
                }

                int totalCount = 0;
                int importCount = 0;

                String fileData = SerializeHelper.readFile(fantasticPlayersFile, Charsets.UTF_8);
                String[] lines = fileData.split("\r\n");
                for (String line : lines) {
                    if (!StringUtils.isNullOrEmpty(line) && line.contains(" = ")) {
                        totalCount++;
                        try {
                            String[] split = line.split(" = ");
                            GameProfile gameProfile = new GameProfile(UUID.fromString(split[0]), split[1]);

                            if (importPlayer(gameProfile)) {
                                player.sendMessage(new TextComponentString(String.format("Importing user profile %s.", gameProfile.getName())));
                                importCount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                player.sendMessage(new TextComponentString(String.format("Imported %d profile(s) of %d total.", importCount, totalCount)));
            }

            private boolean importPlayer(GameProfile gameProfile) {
                if (TablePlayers.isPlayerInDatabase(gameProfile)) {
                    return false;
                } else {
                    TablePlayers.addPlayerToDatabase(gameProfile);
                    return true;
                }
            }
        }));
    }
}
