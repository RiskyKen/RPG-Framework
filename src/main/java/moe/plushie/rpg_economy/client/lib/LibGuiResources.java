package moe.plushie.rpg_economy.client.lib;

import moe.plushie.rpg_economy.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LibGuiResources {
    
    private static final String PREFIX_RESOURCE = LibModInfo.ID + ":textures/gui/";
    
    public static final String MAIL_BOX_TABS = PREFIX_RESOURCE + "mail-box/mail-tabs.png";
    public static final String MAIL_BOX_READING = PREFIX_RESOURCE + "mail-box/reading-mail.png";
    public static final String MAIL_BOX_SENDING = PREFIX_RESOURCE + "mail-box/send-mail.png";
}
