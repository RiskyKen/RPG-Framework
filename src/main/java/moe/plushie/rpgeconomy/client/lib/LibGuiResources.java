package moe.plushie.rpgeconomy.client.lib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class LibGuiResources {
    
    private static final String PREFIX_RESOURCE = LibModInfo.ID + ":textures/gui/";
    
    public static final String MAIL_BOX_READING = PREFIX_RESOURCE + "mail-box/reading-mail.png";
    public static final String MAIL_BOX_SENDING = PREFIX_RESOURCE + "mail-box/send-mail.png";
}
