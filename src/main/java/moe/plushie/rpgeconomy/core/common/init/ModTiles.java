package moe.plushie.rpgeconomy.core.common.init;

import moe.plushie.rpgeconomy.core.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {
    
    public static void registerTileEntities() {
        registerTileEntity(TileEntityMailBox.class, LibBlockNames.MAIL_BOX);
    }
    
    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(LibModInfo.ID, "tileentity." + id));
    }
}
