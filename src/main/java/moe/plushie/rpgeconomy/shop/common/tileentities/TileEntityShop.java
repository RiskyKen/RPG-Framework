package moe.plushie.rpgeconomy.shop.common.tileentities;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.shop.common.Shop;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityShop extends ModAutoSyncTileEntity {

    private static final String TAG_SHOP = "shop";

    private IIdentifier shopIdentifier;

    public TileEntityShop() {
    }

    public TileEntityShop(Shop shop) {
        setShop(shop);
        this.shopIdentifier = shop.getIdentifier();
    }

    public Shop getShop() {
        return RpgEconomy.getProxy().getShopManager().getShop(shopIdentifier);
    }

    public void setShop(IIdentifier shopIdentifier) {
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
            shopIdentifier = IdentifierSerialize.deserializeJson(SerializeHelper.stringToJson(compound.getString(TAG_SHOP)));
        } else {
            shopIdentifier = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (shopIdentifier != null) {
            compound.setString(TAG_SHOP, IdentifierSerialize.serializeJson(shopIdentifier).toString());
        }
        return compound;
    }
}
