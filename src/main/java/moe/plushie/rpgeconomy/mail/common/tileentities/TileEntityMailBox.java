package moe.plushie.rpgeconomy.mail.common.tileentities;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.inventory.IGuiFactory;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpgeconomy.mail.client.gui.GuiMailBox;
import moe.plushie.rpgeconomy.mail.common.MailSystem;
import moe.plushie.rpgeconomy.mail.common.inventory.ContainerMailBox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMailBox extends ModAutoSyncTileEntity implements IGuiFactory {

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
            compound.setString(TAG_MAIL_SYSTEM, mailSystem.getIdentifier());
        }
        return compound;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerMailBox(this, player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiMailBox(this, player);
    }
}
