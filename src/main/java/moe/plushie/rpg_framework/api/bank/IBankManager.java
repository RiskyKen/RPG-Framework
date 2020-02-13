package moe.plushie.rpg_framework.api.bank;

import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IBankManager {

    public IBank getBank(IIdentifier identifier);

    public void getBankAccount(IBankAccountLoadCallback callback, IBank bank, GameProfile sourcePlayer);

    public IBank[] getBanks();

    public String[] getBankNames();

    public static interface IBankAccountLoadCallback {
        public void onBackAccountLoad(IBankAccount bankAccount);
    }
}
