package moe.plushie.rpg_framework.api.core;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.capabilities.Capability;

public interface IStorageDatabase<T> {

    public void writeAsync(Capability<T> capability, T instance, GameProfile player, FutureCallback<Void> callback);
    
    public void readAsync(Capability<T> capability, T instance, GameProfile player, FutureCallback<T> callback);
}
