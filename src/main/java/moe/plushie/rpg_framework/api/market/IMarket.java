package moe.plushie.rpg_framework.api.market;

import java.time.Duration;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IMarket {

    public IIdentifier getIdentifier();

    public String getName();
    
    public BuyAction getBuyAction();
    
    public Duration getMaxListingDuration();

    public enum BuyAction {
        GIVE, MAIL
    }
}
