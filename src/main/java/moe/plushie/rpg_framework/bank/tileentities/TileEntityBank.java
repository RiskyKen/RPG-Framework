package moe.plushie.rpg_framework.bank.tileentities;

import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.bank.ModuleBank;
import moe.plushie.rpg_framework.bank.client.GuiBank;
import moe.plushie.rpg_framework.bank.common.inventory.ContainerBank;
import moe.plushie.rpg_framework.core.common.inventory.IGuiFactory;
import moe.plushie.rpg_framework.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpg_framework.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
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

    private IIdentifier bankIdentifier;

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
        return ModuleBank.getBankManager().getBank(bankIdentifier);
    }

    public void setBank(IBank bank) {
        if (bank != null) {
            bankIdentifier = bank.getIdentifier();
        } else {
            bankIdentifier = null;
        }
        dirtySync();
    }

    public boolean haveBank() {
        return getBank() != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
            JsonElement jsonElement = SerializeHelper.stringToJson(compound.getString(TAG_MAIL_SYSTEM));
            bankIdentifier = IdentifierSerialize.deserializeJson(jsonElement);
        } else {
            bankIdentifier = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (bankIdentifier != null) {
            compound.setString(TAG_MAIL_SYSTEM, IdentifierSerialize.serializeJson(bankIdentifier).toString());
        }
        return compound;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        if (haveBank()) {
            return new ContainerBank(player, getBank(), null);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        if (haveBank()) {
            return new GuiBank(player, getBank());
        } else {
            return null;
        }
    }
}
