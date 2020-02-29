package moe.plushie.rpg_framework.core.common.items;

import java.util.List;

import moe.plushie.rpg_framework.core.common.lib.LibItemNames;
import moe.plushie.rpg_framework.core.common.utils.ItemStackUtils;
import moe.plushie.rpg_framework.core.common.utils.NBTUtils;
import moe.plushie.rpg_framework.core.common.utils.RenderUtils;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCommand extends AbstractModItem {

    private static final String TAG_ITEM = "item";
    private static final String TAG_COMMANDS = "commands";

    public ItemCommand() {
        super(LibItemNames.COMMAND);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        super.getSubItems(tab, items);
        if (isInCreativeTab(tab)) {
            items.add(createStack(new ItemStack(Items.CLOCK), "Add 6000 To Time", "time add 6000"));
            items.add(createStack(ItemStack.EMPTY, "Summon Chicken", "summon minecraft:chicken"));
            items.add(createStack(ItemStack.EMPTY, "BANG", "particle hugeexplosion ~ ~ ~ 0 0 0 0", "playsound minecraft:entity.generic.explode ambient @p"));
            items.add(createStack(ItemStack.EMPTY, "Hiss!!!", "playsound minecraft:entity.creeper.primed hostile @p"));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (haveCommand(stack)) {
            tooltip.add("Command List:");
            String[] commands = getCommands(stack);
            for (String command : commands) {
                tooltip.add(command);
            }
        }
    }

    public ItemStack createStack(ItemStack renderTarget, String displayName, String... commands) {
        ItemStack itemStack = new ItemStack(this);
        if (commands != null && commands.length > 0) {
            setCommands(itemStack, commands);
        }
        if (!renderTarget.isEmpty()) {
            setRenderTarget(itemStack, renderTarget);
        }
        if (!StringUtils.isNullOrEmpty(displayName)) {
            itemStack.setStackDisplayName(displayName);
        }
        return itemStack;
    }

    public ItemStack setRenderTarget(ItemStack itemStack, ItemStack itemStackRender) {
        ItemStackUtils.setItemStackOnItemStack(itemStack, itemStackRender, TAG_ITEM);
        return itemStack;
    }

    public ItemStack getRenderTarget(ItemStack itemStack) {
        return ItemStackUtils.getItemStackFromItemStack(itemStack, TAG_ITEM);
    }

    public boolean haveRenderTarget(ItemStack itemStack) {
        return !getRenderTarget(itemStack).isEmpty();
    }

    public ItemStack setCommands(ItemStack itemStack, String... commands) {
        ItemStackUtils.setTagOnItemStack(itemStack, NBTUtils.createListFromArray(commands), TAG_COMMANDS);
        return itemStack;
    }

    public String[] getCommands(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_COMMANDS, NBT.TAG_LIST)) {
            return NBTUtils.getArrayFromList(itemStack.getTagCompound().getTagList(TAG_COMMANDS, NBT.TAG_STRING));
        }
        return null;
    }

    public boolean haveCommand(ItemStack itemStack) {
        String[] commands = getCommands(itemStack);
        if (commands != null && commands.length > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (haveCommand(itemStack)) {
            String[] commands = getCommands(itemStack);
            boolean commandWorked = false;
            ItemCommandSender commandSender = new ItemCommandSender(playerIn);
            for (String command : commands) {
                if (commandSender.trigger(worldIn, command)) {
                    commandWorked = true;
                }
            }
            if (commandWorked) {
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
            } else {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // TODO Auto-generated method stub
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        // TODO Auto-generated method stub
        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {

            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                ModelResourceLocation mrl = RenderUtils.getMissingModel();
                if (haveRenderTarget(stack)) {
                    mrl = RenderUtils.getModelResourceLocation(getRenderTarget(stack));
                }
                if (mrl == RenderUtils.getMissingModel()) {
                    mrl = modelResourceLocation;
                }
                return mrl;
            }
        });
        ModelBakery.registerItemVariants(this, modelResourceLocation);
    }

    public static class ItemCommandSender implements ICommandSender {

        private final EntityLivingBase entity;

        public ItemCommandSender(EntityLivingBase entity) {
            this.entity = entity;
        }

        @Override
        public String getName() {
            return entity.getName();
        }

        @Override
        public boolean canUseCommand(int permLevel, String commandName) {
            return permLevel <= 2;
        }

        @Override
        public World getEntityWorld() {
            return entity.getEntityWorld();
        }

        @Override
        public BlockPos getPosition() {
            return entity.getPosition();
        }

        @Override
        public Vec3d getPositionVector() {
            return entity.getPositionVector();
        }

        @Override
        public Entity getCommandSenderEntity() {
            return entity;
        }

        @Override
        public MinecraftServer getServer() {
            return getEntityWorld().getMinecraftServer();
        }

        public boolean trigger(World worldIn, String command) {
            if (!worldIn.isRemote) {
                MinecraftServer minecraftserver = this.getServer();

                if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled()) {
                    try {
                        minecraftserver.getCommandManager().executeCommand(this, command);
                        return true;
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Executing command block");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
                        crashreportcategory.addDetail("Command", new ICrashReportDetail<String>() {
                            @Override
                            public String call() throws Exception {
                                return command;
                            }
                        });
                        crashreportcategory.addDetail("Name", new ICrashReportDetail<String>() {
                            @Override
                            public String call() throws Exception {
                                return getName();
                            }
                        });
                        crashreportcategory.addDetail("Entity", new ICrashReportDetail<String>() {
                            @Override
                            public String call() throws Exception {
                                return entity.toString();
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                }

            }
            return false;
        }
    }
}
