package moe.plushie.rpg_framework.core.common.utils;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class PlayerUtils {

    private PlayerUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static boolean gameProfilesMatch(GameProfile profile1, GameProfile profile2) {
        if (profile1 == null) {
            return false;
        }
        if (profile2 == null) {
            return false;
        }

        if (profile1.getId() != null && profile2.getId() != null) {
            if (profile1.getId().equals(profile2.getId())) {
                return true;
            }
        }

        if (profile1.getName() != null && profile2.getName() != null) {
            if (profile1.getName().equals(profile2.getName())) {
                return true;
            }
        }

        return false;
    }

    public static void giveItemToPlayer(EntityPlayer player, ItemStack itemStack) {
        if (!player.inventory.addItemStackToInventory(itemStack.copy())) {
            UtilItems.spawnItemAtEntity(player, itemStack.copy(), true);
        }
    }
}
