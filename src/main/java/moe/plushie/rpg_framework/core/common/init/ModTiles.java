package moe.plushie.rpg_framework.core.common.init;

import moe.plushie.rpg_framework.bank.tileentities.TileEntityBank;
import moe.plushie.rpg_framework.core.common.lib.LibBlockNames;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import moe.plushie.rpg_framework.shop.common.tileentities.TileEntityShop;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {
    
    public static void registerTileEntities() {
        registerTileEntity(TileEntityMailBox.class, LibBlockNames.MAIL_BOX);
        registerTileEntity(TileEntityShop.class, LibBlockNames.SHOP);
        registerTileEntity(TileEntityBank.class, LibBlockNames.BANK);
    }
    
    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(LibModInfo.ID, "tileentity." + id));
    }
}
