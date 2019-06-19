package moe.plushie.rpgeconomy.core.client.lib;

import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LibGuiResources {
    
    private static final String PREFIX_RESOURCE = LibModInfo.ID + ":textures/gui/";
    
    public static final String PLAYER_INVENTORY = PREFIX_RESOURCE + "player_inventory.png";
    public static final String ICONS = PREFIX_RESOURCE + "icons.png";
    public static final String TABS = PREFIX_RESOURCE + "controls/tabs.png";
    
    public static final String MAIL_BOX_TABS = PREFIX_RESOURCE + "mail_box/mail_tabs.png";
    public static final String MAIL_BOX_READING = PREFIX_RESOURCE + "mail_box/reading_mail.png";
    public static final String MAIL_BOX_SENDING = PREFIX_RESOURCE + "mail_box/send_mail.png";
    
    public static final String WALLET = PREFIX_RESOURCE + "wallet/wallet.png";
    public static final String SHOP = PREFIX_RESOURCE + "shop/shop.png";

    
}
