package moe.plushie.rpgeconomy.shop.common.tileentities;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.inventory.IGuiFactory;
import moe.plushie.rpgeconomy.core.common.serialize.IdentifierSerialize;
import moe.plushie.rpgeconomy.core.common.tileentities.ModAutoSyncTileEntity;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.shop.client.gui.GuiShop;
import moe.plushie.rpgeconomy.shop.common.Shop;
import moe.plushie.rpgeconomy.shop.common.inventory.ContainerShop;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityShop extends ModAutoSyncTileEntity implements IGuiFactory {

    private static final String TAG_SHOP = "shop";

    private IIdentifier shopIdentifier;

    public TileEntityShop() {
    }

    public TileEntityShop(Shop shop) {
        setShop(shop);
        this.shopIdentifier = shop.getIdentifier();
    }

    public IShop getShop() {
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
    
    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerShop(player, getShop(), this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiShop(player, true);
    }
}
