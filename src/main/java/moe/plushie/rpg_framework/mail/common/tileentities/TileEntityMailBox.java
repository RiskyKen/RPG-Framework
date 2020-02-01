package moe.plushie.rpg_framework.mail.common.tileentities;

import moe.plushie.rpg_framework.api.core.IIdentifier;
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
    private MailSystem mailSystem;

    public TileEntityMailBox() {
    }

    public TileEntityMailBox(MailSystem mailSystem, MailboxTexture mailboxTexture) {
        this.mailSystem = mailSystem;
        this.mailboxTexture = mailboxTexture;
    }

    public MailSystem getMailSystem() {
        if (mailSystem == null) {
            return RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));
        }
        return mailSystem;
    }

    public void setMailSystem(MailSystem mailSystem) {
        this.mailSystem = mailSystem;
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
            IIdentifier mailSystemName = new IdentifierString(compound.getString(TAG_MAIL_SYSTEM));
            mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(mailSystemName);
        }
        if (compound.hasKey(TAG_MAIL_TEXTURE, NBT.TAG_STRING)) {
            mailboxTexture = MailboxTexture.valueOf(compound.getString(TAG_MAIL_TEXTURE).toUpperCase());
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (mailSystem != null) {
            compound.setString(TAG_MAIL_SYSTEM, (String) mailSystem.getIdentifier().getValue());
        }
        if (mailboxTexture != null) {
            compound.setString(TAG_MAIL_TEXTURE, mailboxTexture.toString());
        }
        return compound;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerMailBox(this, player, getMailSystem());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiMailBox(this, player, getMailSystem());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
