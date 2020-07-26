package moe.plushie.rpg_framework.market.common;

import java.io.File;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.market.IMarket;
import moe.plushie.rpg_framework.api.market.IMarketManager;

public class MarketManager implements IMarketManager {

    private static final String DIRECTORY_NAME = "market";
    
    private final File marketDirectory;
    
    public MarketManager(File modDirectory) {
        marketDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!marketDirectory.exists()) {
            marketDirectory.mkdir();
        }
    }
    
    @Override
    public IMarket getMarket(IIdentifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IMarket[] getMarkets() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getMarketNames() {
        // TODO Auto-generated method stub
        return null;
    }

}
