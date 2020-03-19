package moe.plushie.rpg_framework.core.common.network.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.stats.common.StatsServer;
import moe.plushie.rpg_framework.stats.common.StatsWorld;
import moe.plushie.rpg_framework.stats.common.inventory.ContainerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerStatsUpdate implements IMessage {

    private boolean fullUpdate;
    private StatsServer statsServer;
    private StatsWorld[] statsWorlds;

    public MessageServerStatsUpdate() {
    }

    public MessageServerStatsUpdate(boolean fullUpdate, StatsServer statsServer, StatsWorld... statsWorlds) {
        this.fullUpdate = fullUpdate;
        this.statsServer = statsServer;
        this.statsWorlds = statsWorlds;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(statsServer.getLoadedChunks());
        buf.writeInt(statsServer.getPlayersOnline());
        buf.writeInt(statsServer.getMemUseMB());
        int[] serverTickHistory = null;
        if (fullUpdate) {
            serverTickHistory = statsServer.getHistoryTickTime().getFullHistory();
        } else {
            serverTickHistory = statsServer.getHistoryTickTime().getShortHistory();
        }
        ByteBufHelper.writeIntArray(buf, serverTickHistory);

        buf.writeInt(statsWorlds.length);
        for (StatsWorld statsWorld : statsWorlds) {
            buf.writeInt(statsWorld.getDimensionID());
            buf.writeInt(statsWorld.getPlayersCount());
            buf.writeInt(statsWorld.getEntityCount());
            buf.writeInt(statsWorld.getTileCount());
            buf.writeInt(statsWorld.getTickingTileCount());
            int[] worldTickHistory = null;
            if (fullUpdate) {
                worldTickHistory = statsWorld.getHistoryTickTime().getFullHistory();
            } else {
                worldTickHistory = statsWorld.getHistoryTickTime().getShortHistory();
            }
            ByteBufHelper.writeIntArray(buf, worldTickHistory);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int loadedChunks = buf.readInt();
        int playersOnline = buf.readInt();
        int memUseMB = buf.readInt();
        int[] serverTickHistory = ByteBufHelper.readIntArray(buf);
        statsServer = new StatsServer(1);
        statsServer.updateStats(serverTickHistory, loadedChunks, playersOnline, memUseMB);

        int worldCount = buf.readInt();
        statsWorlds = new StatsWorld[worldCount];
        for (int i = 0; i < statsWorlds.length; i++) {
            int dimensionID = buf.readInt();
            int playersCount = buf.readInt();
            int entityCount = buf.readInt();
            int tileCount = buf.readInt();
            int tickingTileCount = buf.readInt();
            int[] worldTickHistory = ByteBufHelper.readIntArray(buf);
            statsWorlds[i] = new StatsWorld(dimensionID, 1);
            statsWorlds[i].updateStats(worldTickHistory, playersCount, entityCount, tileCount, tickingTileCount);
        }
    }

    public static class Handler implements IMessageHandler<MessageServerStatsUpdate, IMessage> {

        @Override
        public IMessage onMessage(MessageServerStatsUpdate message, MessageContext ctx) {
            updateStats(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void updateStats(MessageServerStatsUpdate message) {
            Container container = Minecraft.getMinecraft().player.openContainer;
            if (container != null && container instanceof ContainerStats) {
                ((ContainerStats) container).gotServerUpdate(message.statsServer, message.statsWorlds);
            }
        }
    }
}
