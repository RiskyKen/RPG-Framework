package moe.plushie.rpg_framework.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.utils.ByteBufHelper;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncBankAccount implements IMessage, IMessageHandler<MessageServerSyncBankAccount, IMessage> {

    private IBankAccount[] bankAccounts;

    public MessageServerSyncBankAccount() {
    }

    public MessageServerSyncBankAccount(IBankAccount... bankAccount) {
        this.bankAccounts = bankAccount;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(bankAccounts.length);
        for (int i = 0; i < bankAccounts.length; i++) {
            ByteBufHelper.writeIdentifier(buf, bankAccounts[i].getBank().getIdentifier());
            JsonElement json = BankAccountSerializer.serializeJson(bankAccounts[i], false);
            ByteBufUtils.writeUTF8String(buf, json.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        bankAccounts = new IBankAccount[buf.readInt()];
        for (int i = 0; i < bankAccounts.length; i++) {
            IIdentifier bankIdentifier = ByteBufHelper.readIdentifier(buf);
            IBank bank = RPGFramework.getProxy().getBankManager().getBank(bankIdentifier);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement json = SerializeHelper.stringToJson(jsonString);
            bankAccounts[i] = BankAccountSerializer.deserializeJson(json, bank);
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncBankAccount message, MessageContext ctx) {
        setBankAccounts(message.bankAccounts);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void setBankAccounts(IBankAccount[] bankAccounts) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                /*
                EntityPlayer player = Minecraft.getMinecraft().player;
                IBankCapability capability = BankCapability.get(player);
                if (capability != null) {
                    for (int i = 0; i < bankAccounts.length; i++) {
                        capability.setBankAccount(bankAccounts[i]);
                    }
                }
                */
            }
        });
    }
}
