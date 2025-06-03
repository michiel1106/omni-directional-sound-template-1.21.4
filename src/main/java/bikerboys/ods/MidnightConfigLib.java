package bikerboys.ods;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;

import java.util.List;

public class MidnightConfigLib extends MidnightConfig {



    @Entry(name = "Arrow Offset") public static int arrowOffset = 20;

    @Entry(name = "Arrow Scale") public static int arrowScale = 1;
    @Entry(name = "Arrow Display Mode") public static ArrowEnum arrowEnum = ArrowEnum.STATIC;
    @Entry(name = "Whitelist/Blacklist") public static ListEnum listEnum = ListEnum.BLACKLIST;
    @Entry(name = "Arrow Disappear (ms)") public static int arrowFade = 500;
    @Entry(name = "Max Sound Range") public static int maxSoundRange = 32;
    @Entry(name = "Colored arrows") public static boolean coloredArrows = true;

    @Entry(name = "X Offset", min = -1000000000) public static float xOffset = 0;
    @Entry(name = "Y Offset", min = -1000000000) public static float yOffset = 0;
    @Entry(name = "Edit Mode") public static boolean editMode = false;



    public enum ArrowEnum {
        STATIC, DYNAMIC
    }

    public enum ListEnum {
        WHITELIST, BLACKLIST
    }

    @Comment() public static String comment;
    @Entry(name = "Ignored Sounds", width = 600) public static List<String> ignoredSounds = Lists.newArrayList(
            "minecraft:block.stone.step",
            "minecraft:block.grass.step",
            "minecraft:block.sand.step",
            "minecraft:block.snow.step",
            "minecraft:block.wood.step",
            "minecraft:block.gravel.step",
            "minecraft:block.stone_bricks.step",
            "minecraft:block.wool.step",
            "minecraft:block.iron.step",
            "minecraft:block.ladder.step",
            "minecraft:block.scaffolding.step",
            "minecraft:block.soul_sand.step",
            "minecraft:block.nether_wart.step",
            "minecraft:block.bamboo.step",
            "minecraft:block.basalt.step",
            "minecraft:block.amethyst.step",
            "minecraft:block.nylium.step",
            "minecraft:block.ancient_debris.step",
            "minecraft:block.slime_block.step",
            "minecraft:block.honey_block.step",
            "minecraft:block.sculk.step",
            "minecraft:block.moss.step",
            "minecraft:block.big_dripleaf.step",
            "minecraft:block.coral_block.step",
            "minecraft:block.ice.step",
            "minecraft:block.glass.step",
            "minecraft:block.wet_grass.step",
            "minecraft:block.deepslate.step",
            "minecraft:block.nether_bricks.step",
            "minecraft:block.end_stone.step",
            "minecraft:block.bone_block.step",
            "minecraft:block.chain.step",
            "minecraft:block.fungus.step",
            "minecraft:block.vine.step",
            "minecraft:block.lava.step",
            "minecraft:block.water.step",
            "minecraft:block.generic.step");


    @Entry(name = "Sound Angle") public static boolean soundAngle = false;
    @Entry(name = "Test Int", min = -100000) public static float testInt = 0;
    @Entry(name = "Test Int2", min = -100000) public static int testInt2 = 0;

}
