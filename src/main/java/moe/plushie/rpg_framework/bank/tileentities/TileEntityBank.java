package moe.plushie.rpg_framework.bank.tileentities;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.bank.client.GuiBank;
import moe.plushie.rpg_framework.bank.common.inventory.ContainerBank;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.inventory.IGuiFactory;
import moe.plushie.rpg_framework.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpg_framework.core.database.DBPlayer;
import moe.plushie.rpg_framework.core.database.TablePlayers;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBank extends ModAutoSyncTileEntity implements IGuiFactory {
    
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
        return RPGFramework.getProxy().getBankManager().getBank(bankIdentifier);
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

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        DBPlayer dbPlayer = TablePlayers.getPlayer(player.getGameProfile());
        return new ContainerBank(player, getBank(), dbPlayer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiBank(player, getBank());
    }
}
