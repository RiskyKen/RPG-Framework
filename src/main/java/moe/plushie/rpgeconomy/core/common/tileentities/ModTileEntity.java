package moe.plushie.rpgeconomy.core.common.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class ModTileEntity extends TileEntity {
    
    private boolean sync = true;
    
    public void enableSync() {
        sync = true;
    }

    public void disableSync() {
        sync = false;
    }
    
    /**
     * Sync the tile entity with the clients.
     */
    public void syncWithClients() {
        if (!sync) {
            return;
        }
        if (getWorld() == null) {
            return;
        }
        if (!getWorld().isRemote) {
            syncWithNearbyPlayers(this);
        } else {
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }
    
    /**
     * Marks the tile entity as dirty and sync it with the clients.
     */
    public void dirtySync() {
        markDirty();
        syncWithClients();
    }
    
    public static void syncWithNearbyPlayers(TileEntity tileEntity) {
        if (tileEntity.getWorld() == null) {
            return;
        }
        if (!(tileEntity.getWorld() instanceof WorldServer)) {
            return;
        }
        WorldServer worldServer = (WorldServer) tileEntity.getWorld();
        PlayerChunkMapEntry chunk = worldServer.getPlayerChunkMap().getEntry(tileEntity.getPos().getX() >> 4, tileEntity.getPos().getZ() >> 4);
        SPacketUpdateTileEntity packet = tileEntity.getUpdatePacket();
        if (chunk != null & packet != null) {
            chunk.sendPacket(packet);
        }
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
