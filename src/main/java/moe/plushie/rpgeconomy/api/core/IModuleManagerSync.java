package moe.plushie.rpgeconomy.api.core;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IModuleManagerSync<T> extends IModuleManager<T> {

    public void syncToAllClients();
    
    public void syncToClient(EntityPlayerMP entityPlayer);
}
