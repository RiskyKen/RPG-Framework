package moe.plushie.rpgeconomy.bank.tileentities;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityBank extends ModAutoSyncTileEntity {
    
    private static final String TAG_MAIL_SYSTEM = "mailSystem";

    private String bankIdentifier;

    public TileEntityBank() {
    }

    public TileEntityBank(IBank bank) {
        if (bank != null) {
            bankIdentifier = bank.getIdentifier();
        } else {
            bankIdentifier = null;
        }
    }

    public IBank getBank() {
        return RpgEconomy.getProxy().getBankManager().getBank(bankIdentifier);
    }

    public void setBank(IBank bank) {
        if (bank != null) {
            bankIdentifier = bank.getIdentifier();
        } else {
            bankIdentifier = null;
        }
        dirtySync();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
            bankIdentifier = compound.getString(TAG_MAIL_SYSTEM);
        } else {
            bankIdentifier = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (bankIdentifier != null) {
            compound.setString(TAG_MAIL_SYSTEM, bankIdentifier);
        }
        return compound;
    }
}
