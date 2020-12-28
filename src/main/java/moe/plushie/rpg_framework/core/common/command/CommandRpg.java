package moe.plushie.rpg_framework.core.common.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.common.BankAccount;
import moe.plushie.rpg_framework.bank.common.command.CommandBank;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.command.CommandExecute.ICommandExecute;
import moe.plushie.rpg_framework.core.common.database.DBPlayerInfo;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.database.TablePlayers;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerCommand;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerCommand.ServerCommandType;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
import moe.plushie.rpg_framework.currency.common.command.CommandCurrency;
import moe.plushie.rpg_framework.itemData.command.CommandItemData;
import moe.plushie.rpg_framework.loot.common.command.CommandLoot;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.TableMail;
import moe.plushie.rpg_framework.mail.common.command.CommandMail;
import moe.plushie.rpg_framework.shop.common.command.CommandShop;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandRpg extends ModSubCommands {

    public CommandRpg() {
        super(null, LibModInfo.ID);
        /*addSubCommand(new CommandExecute(this, "manager", new ICommandExecute() {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                FMLNetworkHandler.openGui(player, RpgEconomy.getInstance(), EnumGuiId.MANAGER.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));*/
        addSubCommand(new CommandCurrency(this));
        addSubCommand(new CommandMail(this));
        addSubCommand(new CommandShop(this));
        addSubCommand(new CommandDev(this));
        addSubCommand(new CommandBank(this));
        addSubCommand(new CommandIntegration(this));
        addSubCommand(new CommandLoot(this));
        addSubCommand(new CommandItemData(this));
        addSubCommand(new CommandExecute(this, "open_folder", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerCommand(ServerCommandType.OPEN_PACK_FOLDER), player);
            }
        }));
        addSubCommand(new CommandExecute(this, "stats", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                FMLNetworkHandler.openGui(player, RPGFramework.getInstance(), EnumGuiId.STATS.ordinal(), server.getEntityWorld(), 0, 0, 0);
            }
        }));
        addSubCommand(new CommandExecute(this, "reset_player", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                String playerText = args[getParentCount() + 1];

                EntityPlayerMP targetPlayer = getPlayer(server, sender, playerText);
                ICurrencyCapability currencyCapability = CurrencyCapability.get(player);
                if (currencyCapability != null) {
                    for (ICurrency currency : RPGFramework.getProxy().getCurrencyManager().getCurrencies()) {
                        currencyCapability.getWallet(currency).setAmount(0);
                    }
                    currencyCapability.syncToOwner(targetPlayer, true);
                }
                DatabaseManager.createTaskAndExecute(new Runnable() {

                    @Override
                    public void run() {
                        DBPlayerInfo playerInfo = TablePlayers.getPlayerInfo(targetPlayer.getGameProfile());
                        if (playerInfo.isMissing()) {
                            return;
                            // throw new WrongUsageException(getUsage(sender), (Object) args);
                        }
                        for (IBank bank : ModuleBank.getBankManager().getBanks()) {
                            BankAccount bankAccount = BankAccountSerializer.deserializeDatabase(playerInfo, bank);
                            bankAccount.setNewAccount();
                            BankAccountSerializer.serializeDatabase(playerInfo, bankAccount);
                            bankAccount.syncToOwner(targetPlayer);
                        }
                        for (IMailSystem mailSystem : RPGFramework.getProxy().getMailSystemManager().getMailSystems()) {
                            for (MailMessage mailMessage : TableMail.getMessages(targetPlayer, mailSystem)) {
                                TableMail.deleteMessage(mailMessage.getId());
                            }
                        }
                        RPGFramework.getProxy().getMailSystemManager().getNotificationManager().syncToClient(targetPlayer, false, false);
                    }
                }, new FutureCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                            @Override
                            public void run() {
                                player.sendMessage(new TextComponentString("Player was reset."));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                            @Override
                            public void run() {
                                player.sendMessage(new TextComponentString("Player reset failed."));
                            }
                        });
                    }
                });
            }
        }));
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<String>();
        aliases.add("rpg");
        return aliases;
    }
}
