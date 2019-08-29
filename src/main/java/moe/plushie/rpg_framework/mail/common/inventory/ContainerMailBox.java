package moe.plushie.rpg_framework.mail.common.inventory;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RpgEconomy;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.inventory.ModTileContainer;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerMailList;
import moe.plushie.rpg_framework.core.database.TableMail;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import moe.plushie.rpg_framework.mail.common.MailSystem;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;

public class ContainerMailBox extends ModTileContainer<TileEntityMailBox> {

    private boolean synced = false;
    
    public ContainerMailBox(TileEntityMailBox tileEntity, EntityPlayer entityPlayer) {
        super(entityPlayer, tileEntity);
        addPlayerSlots(8, 167);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
    
    public void markDirty() {
        synced = false;
    }

    @Override
    public void detectAndSendChanges() {
        MailSystem mailSystem = RpgEconomy.getProxy().getMailSystemManager().getMailSystem(new IdentifierString("main.json"));
        super.detectAndSendChanges();
        if (!synced) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) listener;
                    ArrayList<MailMessage> mailMessages = TableMail.getMessages(player, mailSystem);
                    MessageServerMailList message = new MessageServerMailList(mailMessages);
                    PacketHandler.NETWORK_WRAPPER.sendTo(message, player);
                }
            }
            synced = true;
        }
    }
}
