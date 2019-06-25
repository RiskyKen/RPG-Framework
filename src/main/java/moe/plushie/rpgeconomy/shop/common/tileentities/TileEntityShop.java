package moe.plushie.rpgeconomy.shop.common.tileentities;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpgeconomy.shop.common.Shop;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityShop extends ModAutoSyncTileEntity {

    private static final String TAG_SHOP = "shop";

    private String shopIdentifier;

    public TileEntityShop() {
    }

    public TileEntityShop(Shop shop) {
        setShop(shop);
        this.shopIdentifier = shop.getIdentifier();
    }

    public Shop getShop() {
        return RpgEconomy.getProxy().getShopManager().getShop(shopIdentifier);
    }
    
    public void setShop(String shopIdentifier) {
        this.shopIdentifier = shopIdentifier;
        dirtySync();
    }
    
    public void setShop(Shop shop) {
        this.shopIdentifier = shop.getIdentifier();
        dirtySync();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_SHOP, NBT.TAG_STRING)) {
            shopIdentifier = compound.getString(TAG_SHOP);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (!StringUtils.isNullOrEmpty(shopIdentifier)) {
            compound.setString(TAG_SHOP, shopIdentifier);
        }
        return compound;
    }
}
