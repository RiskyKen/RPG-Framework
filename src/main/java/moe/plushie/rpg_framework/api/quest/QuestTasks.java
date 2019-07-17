package moe.plushie.rpg_framework.api.quest;

public enum QuestTasks {
    /** Player has the item in their inventory. */
    ITEM_HAVE,
    /** Player picks up the item. */
    ITEM_PICKUP,
    /** Player right click the item. */
    ITEM_USE,
    /** Player crafts the item. */
    ITEM_CRAFT,
    /** Player has discovered this location before. */
    LOCATION_DISCOVER,
    /** Player been to this location when the quest is active. */
    LOCATION_GOTO,
    /** Player has killed this mob before. */
    MOB_KILLED,
    /** Player has killed this mob when the quest is active. */
    MOB_KILL,
    /** Player has seen this mob before. */
    MOB_SEEN,
    /** Player has seen this mob when the quest is active. */
    MOB_FIND,
    /** Player reads a mail message. */
    MAIL_READ,
    /** Player sends a mail message. */
    MAIL_SEAD,
    /** Player breaks a block. */
    BLOCK_BREAK,
    /** Player places a block. */
    BLOCK_PLACE,
    /** Player places a block at a location. */
    BLOCK_PLACE_LOCATION
}
