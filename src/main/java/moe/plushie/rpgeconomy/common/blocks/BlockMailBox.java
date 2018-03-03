package moe.plushie.rpgeconomy.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import moe.plushie.rpgeconomy.common.items.block.ModItemBlock;
import moe.plushie.rpgeconomy.common.lib.LibBlockNames;
import net.minecraft.block.Block;

public class BlockMailBox extends AbstractModBlock {

    public BlockMailBox() {
        super(LibBlockNames.MAIL_BOX);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
}
