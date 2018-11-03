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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
            //player.addChatComponentMessage(new ChatComponentText("Not implemented yet, check back later."));
            FMLNetworkHandler.openGui(player, RPGEconomy.getInstance(), LibGuiIds.MAIL_BOX, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
        int rot = MathHelper.floor_double((double)(livingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ForgeDirection rots[] = new ForgeDirection[] {ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST};
        ForgeDirection direction = rots[rot].getOpposite();
        RPGEconomy.getLogger().info("Rotation " + rot);
        world.setBlockMetadataWithNotify(x, y, z, direction.ordinal(), 3);
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_FRONT);
        iconSide = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_SIDE);
        iconSideFlag = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_SIDE_FLAG);
        iconTopBot = iconRegister.registerIcon(LibBlockResources.MAIL_BOX_TOP_BOT);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        ForgeDirection direction = ForgeDirection.getOrientation(meta);
        if (side == meta) {
            return blockIcon;
        }
        if (side < 2) {
            return iconTopBot;
        }
        if (side == direction.getRotation(ForgeDirection.DOWN).ordinal()) {
            return iconSideFlag;
        }
        return iconSide;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        ForgeDirection direction = ForgeDirection.getOrientation(meta);
        if (side == 3) {
            return blockIcon;
        }
        if (side < 2) {
            return iconTopBot;
        }
        if (side == 5) {
            return iconSideFlag;
        }
        return iconSide;
    }
}
