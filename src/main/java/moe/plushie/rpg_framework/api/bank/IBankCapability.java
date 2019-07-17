package moe.plushie.rpg_framework.api.bank;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IBankCapability {

    public IBankAccount getBank(IBank bank);

    public void setBankAccount(IBankAccount bankInstance);
    
    public void syncToOwner(EntityPlayerMP entityPlayer);
}
