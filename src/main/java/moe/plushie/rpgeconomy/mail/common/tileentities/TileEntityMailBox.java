package moe.plushie.rpgeconomy.mail.common.tileentities;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.tileentities.ModTileEntity;
import moe.plushie.rpgeconomy.mail.common.MailSystem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityMailBox extends ModTileEntity {
    
    private static final String TAG_MAIL_SYSTEM = "mailSystem";
    
    private MailSystem mailSystem;
    
    public TileEntityMailBox() {
    }
    
    public TileEntityMailBox(MailSystem mailSystem) {
        this.mailSystem = mailSystem;
    }
    
    public MailSystem getMailSystem() {
        return mailSystem;
    }
    
    public void setMailSystem(MailSystem mailSystem) {
        this.mailSystem = mailSystem;
        dirtySync();
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        RpgEconomy.getLogger().info("reading: " + compound);
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
            String mailSystemName = compound.getString(TAG_MAIL_SYSTEM);
            mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(mailSystemName);
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (mailSystem != null) {
            compound.setString(TAG_MAIL_SYSTEM, mailSystem.getName());
        }
        RpgEconomy.getLogger().info("writing: " + compound);
        return compound;
    }
}
