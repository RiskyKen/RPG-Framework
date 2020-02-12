package moe.plushie.rpg_framework.core.client.lib;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LibGuiResources {

    private static final String PREFIX_TEXTURES = LibModInfo.ID + ":textures/gui/";
    private static final String PREFIX_JSON = LibModInfo.ID + ":gui/";

    public static final String PLAYER_INVENTORY = PREFIX_TEXTURES + "player_inventory.png";
    public static final String BACKGROUND = PREFIX_TEXTURES + "background.png";
    public static final String ICONS = PREFIX_TEXTURES + "icons.png";
    public static final String TABS = PREFIX_TEXTURES + "controls/tabs.png";
    public static final String BUTTONS = PREFIX_TEXTURES + "controls/buttons.png";

    public static final String MAIL_BOX = PREFIX_TEXTURES + "mail_box/mail_box.png";
    // public static final String MAIL_BOX_SENDING = PREFIX_RESOURCE + "mail_box/send_mail.png";

    public static final String WALLET = PREFIX_TEXTURES + "wallet/wallet.png";
    public static final String SHOP = PREFIX_TEXTURES + "shop/shop.png";
    public static final String BANK = PREFIX_TEXTURES + "bank/bank.png";

    public class Controls {

        private static final String PREFIX = "inventory." + LibModInfo.ID + ":common.";

        public static final String BUTTON_CLOSE = PREFIX + "button_close";
        public static final String BUTTON_CANCEL = PREFIX + "button_cancel";
        public static final String BUTTON_OK = PREFIX + "button_ok";
    }
}
