package moe.plushie.rpg_framework.api.core;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IModuleManagerSync<T> extends IModuleManager<T> {

    public void syncToAllClients();
    
    public void syncToClient(EntityPlayerMP entityPlayer);
}
