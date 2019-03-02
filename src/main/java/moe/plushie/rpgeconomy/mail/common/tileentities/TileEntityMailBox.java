package moe.plushie.rpgeconomy.mail.common.tileentities;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpgeconomy.mail.common.MailSystem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityMailBox extends ModAutoSyncTileEntity {
    
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
    public void readFromNBT(NBTTagCompound compound) {
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
        return compound;
    }
}
