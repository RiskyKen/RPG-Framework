package moe.plushie.rpg_framework.api.market;

import moe.plushie.rpg_framework.api.core.IIdentifier;

public interface IMarketManager {

    public IMarket getMarket(IIdentifier identifier);

    public IMarket[] getMarkets();

    public String[] getMarketNames();
}
