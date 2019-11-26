package moe.plushie.rpg_framework.core.common.network.server;

import java.util.ArrayList;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.core.client.gui.manager.GuiManager;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.loot.client.gui.GuiLootEditor;
import moe.plushie.rpg_framework.mail.client.gui.GuiMailBox;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerMailResult implements IMessage {

    private ArrayList<GameProfile> success = new ArrayList<GameProfile>();
    private ArrayList<GameProfile> failed = new ArrayList<GameProfile>();

    public MessageServerMailResult() {
    }

    public MessageServerMailResult(ArrayList<GameProfile> success, ArrayList<GameProfile> failed) {
        this.success = success;
        this.failed = failed;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(success.size());
        for (int i = 0; i < success.size(); i++) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTUtil.writeGameProfile(compound, success.get(i));
            ByteBufUtils.writeTag(buf, compound);
        }
        buf.writeInt(failed.size());
        for (int i = 0; i < failed.size(); i++) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTUtil.writeGameProfile(compound, failed.get(i));
            ByteBufUtils.writeTag(buf, compound);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int successCount = buf.readInt();
        for (int i = 0; i < successCount; i++) {
            NBTTagCompound compound = ByteBufUtils.readTag(buf);
            success.add(NBTUtil.readGameProfileFromNBT(compound));
        }
        int failedCount = buf.readInt();
        for (int i = 0; i < failedCount; i++) {
            NBTTagCompound compound = ByteBufUtils.readTag(buf);
            failed.add(NBTUtil.readGameProfileFromNBT(compound));
        }
    }

    public static class Handler implements IMessageHandler<MessageServerMailResult, IMessage> {

        @Override
        public IMessage onMessage(MessageServerMailResult message, MessageContext ctx) {
            sendToGui(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void sendToGui(MessageServerMailResult message) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (mc.currentScreen != null && mc.currentScreen instanceof GuiMailBox) {
                        ((GuiMailBox) mc.currentScreen).onServerMailResult(message.success, message.failed);
                    }
                }
            });
        }
    }
}
