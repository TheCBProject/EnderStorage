package codechicken.enderstorage.api;

/**
 * Created by covers1624 on 5/7/2016.
 *///TODO Merge to lib.
public enum Colour {

    WHITE("white", "White", "item.fireworksCharge.white"),
    ORANGE("orange", "Orange", "item.fireworksCharge.orange"),
    MAGENTA("magenta", "Magenta", "item.fireworksCharge.magenta"),
    LIGHT_BLUE("light_blue", "LightBlue", "item.fireworksCharge.lightBlue"),
    YELLOW("yellow", "Yellow", "item.fireworksCharge.yellow"),
    LIME("lime", "Lime", "item.fireworksCharge.lime"),
    PINK("pink", "Pink", "item.fireworksCharge.pink"),
    GRAY("gray", "Gray", "item.fireworksCharge.gray"),
    LIGHT_GRAY("light_gray", "Light_Gray", "item.fireworksCharge.silver"),
    CYAN("cyan", "Cyan", "item.fireworksCharge.cyan"),
    PURPLE("purple", "Purple", "item.fireworksCharge.purple"),
    BLUE("blue", "Blue", "item.fireworksCharge.blue"),
    BROWN("brown", "Brown", "item.fireworksCharge.brown"),
    GREEN("green", "Green", "item.fireworksCharge.green"),
    RED("red", "Red", "item.fireworksCharge.red"),
    BLACK("black", "Black", "item.fireworksCharge.black");

    private String minecraftName;
    private String oreDictionaryName;
    private String unlocalizedName;
    private int hex;

    Colour(String minecraftName, String oreDictionaryName, String unlocalizedName) {
        this.minecraftName = minecraftName;
        this.oreDictionaryName = oreDictionaryName;
        this.unlocalizedName = unlocalizedName;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public String getOreDictionaryName() {
        return oreDictionaryName;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }
}
