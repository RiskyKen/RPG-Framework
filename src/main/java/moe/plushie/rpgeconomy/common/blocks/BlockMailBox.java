package moe.plushie.rpgeconomy.common.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.rpgeconomy.RPGEconomy;
import moe.plushie.rpgeconomy.client.lib.LibBlockResources;
import moe.plushie.rpgeconomy.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockMailBox extends AbstractModBlockContainer {

    public BlockMailBox() {
        super(LibBlockNames.MAIL_BOX);
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
    
    @SideOnly(Side.CLIENT)
    IIcon iconSide;
    @SideOnly(Side.CLIENT)
    IIcon iconSideFlag;
    @SideOnly(Side.CLIENT)
    IIcon iconTopBot;
    
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_FRONT);
        iconSide = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_SIDE);
        iconSideFlag = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_SIDE_FLAG);
        iconTopBot = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_TOP_BOT);
    }
    
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4) {
            return blockIcon;
        }
        if (side < 2) {
            return iconTopBot;
        }
        if (side == 2) {
            return iconSideFlag;
        }
        return iconSide;
    }
}
