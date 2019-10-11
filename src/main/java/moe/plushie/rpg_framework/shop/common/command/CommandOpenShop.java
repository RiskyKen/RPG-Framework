package moe.plushie.rpg_framework.shop.common.command;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierInt;
import moe.plushie.rpg_framework.core.common.command.ModCommand;
import moe.plushie.rpg_framework.core.common.lib.EnumGuiId;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerShop;
import moe.plushie.rpg_framework.core.database.TableShops;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class CommandOpenShop extends ModCommand {

    public CommandOpenShop(ModCommand parent, String name) {
        super(parent, name);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            ArrayList<IIdentifier> identifier = new ArrayList<IIdentifier>();
            TableShops.getShopList(identifier, null, null);
            String[] ids = new String[identifier.size()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = String.valueOf(identifier.get(i).getValue());
            }
            return getListOfStringsMatchingLastWord(args, ids);
        }
        if (args.length == getParentCount() + 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        int id = parseInt(args[getParentCount()]);
        
        
        EntityPlayer player = null;
        if (args.length < getParentCount() + 2) {
            player = getCommandSenderAsPlayer(sender);
        } else {
            player = getPlayer(server, sender, args[getParentCount() + 1]);
        }
        
        IShop shop = RPGFramework.getProxy().getShopManager().getShop(new IdentifierInt(id));
        FMLNetworkHandler.openGui(player, RPGFramework.getInstance(), EnumGuiId.SHOP_COMMAND.ordinal(), server.getEntityWorld(), id, 0, 0);
        
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerShop(shop, false), (EntityPlayerMP) player);
    }
}
