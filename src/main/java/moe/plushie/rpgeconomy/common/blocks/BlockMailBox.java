package moe.plushie.rpgeconomy.common.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import moe.plushie.rpgeconomy.RPGEconomy;
import moe.plushie.rpgeconomy.common.items.block.ModItemBlock;
import moe.plushie.rpgeconomy.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMailBox extends AbstractModBlockContainer {

    public BlockMailBox() {
        super(LibBlockNames.MAIL_BOX);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, RPGEconomy.getInstance(), LibGuiIds.MAIL_BOX, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMailBox();
    }
}
