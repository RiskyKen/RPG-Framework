package moe.plushie.rpg_framework.mail.common.tileentities;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.inventory.IGuiFactory;
import moe.plushie.rpg_framework.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.blocks.BlockMailBox.MailboxTexture;
import moe.plushie.rpg_framework.mail.common.inventory.ContainerMailBox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMailBox extends ModAutoSyncTileEntity implements IGuiFactory {

    private static final String TAG_MAIL_SYSTEM = "mailSystem";
    private static final String TAG_MAIL_TEXTURE = "texture";

    private MailboxTexture mailboxTexture;
    private IIdentifier mailSystemIdentifier;

    public TileEntityMailBox() {
    }

    public TileEntityMailBox(IIdentifier identifier, MailboxTexture mailboxTexture) {
        this.mailSystemIdentifier = identifier;
        this.mailboxTexture = mailboxTexture;
    }

    public MailSystem getMailSystem() {
        if (mailSystemIdentifier != null) {
            return RPGFramework.getProxy().getMailSystemManager().getMailSystem(mailSystemIdentifier);
        }
        return null;
    }

    public void setMailSystem(IIdentifier identifier) {
        this.mailSystemIdentifier = identifier;
        dirtySync();
    }

    public void setMailboxTexture(MailboxTexture mailboxTexture) {
        this.mailboxTexture = mailboxTexture;
        dirtySync();
    }

    public MailboxTexture getMailboxTexture() {
        return mailboxTexture;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_MAIL_SYSTEM, NBT.TAG_STRING)) {
            mailSystemIdentifier = new IdentifierString(compound.getString(TAG_MAIL_SYSTEM));
        }
        if (compound.hasKey(TAG_MAIL_TEXTURE, NBT.TAG_STRING)) {
            mailboxTexture = MailboxTexture.valueOf(compound.getString(TAG_MAIL_TEXTURE).toUpperCase());
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (mailSystemIdentifier != null) {
            compound.setString(TAG_MAIL_SYSTEM, (String) mailSystemIdentifier.getValue());
        }
        if (mailboxTexture != null) {
            compound.setString(TAG_MAIL_TEXTURE, mailboxTexture.toString());
        }
        return compound;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        if (getMailSystem() != null) {
            return new ContainerMailBox(this, player, getMailSystem());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        if (getMailSystem() != null) {
            return new GuiMailBox(this, player, getMailSystem());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        IMailSystem mailSystem = getMailSystem();
        if (mailSystem != null) {
            if (!mailSystem.isMailboxFlagRender()) {
                return 0;
            }
            return getMailSystem().getMailboxFlagRenderDistance() * getMailSystem().getMailboxFlagRenderDistance();
        }
        return super.getMaxRenderDistanceSquared();
    }
}
