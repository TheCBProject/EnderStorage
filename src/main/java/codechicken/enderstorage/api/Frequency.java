package codechicken.enderstorage.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by covers1624 on 4/26/2016.
 */
public final class Frequency {

    public static final String[] colours = new String[] { "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public int left;
    public int middle;
    public int right;

    public Frequency() {
        this(0, 0, 0);
    }

    public Frequency(int left, int middle, int right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public Frequency setLeft(int left) {
        this.left = left;
        return this;
    }

    public Frequency setMiddle(int middle) {
        this.middle = middle;
        return this;
    }

    public Frequency setRight(int right) {
        this.right = right;
        return this;
    }

    public static Frequency fromArray(int[] colours) {
        Frequency frequency = new Frequency();
        frequency.setLeft(colours[0]);
        frequency.setMiddle(colours[1]);
        frequency.setRight(colours[2]);
        return frequency;
    }

    public Frequency setFrequency(int left, int middle, int right) {
        setLeft(left);
        setMiddle(middle);
        setRight(right);
        return this;
    }

    public Frequency setFrequency(Frequency frequency) {
        setLeft(frequency.left);
        setMiddle(frequency.middle);
        setRight(frequency.right);
        return this;
    }

    public String getLeft() {
        return colours[left];
    }

    public String getMiddle() {
        return colours[middle];
    }

    public String getRight() {
        return colours[right];
    }

    public String[] getColours() {
        return new String[] { getLeft(), getMiddle(), getRight() };
    }

    public int[] toArray() {
        return new int[] { left, middle, right };
    }

    public Frequency readNBT(NBTTagCompound tagCompound) {
        left = tagCompound.getInteger("left");
        middle = tagCompound.getInteger("middle");
        right = tagCompound.getInteger("right");
        return this;
    }

    public Frequency writeNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("left", left);
        tagCompound.setInteger("middle", middle);
        tagCompound.setInteger("right", right);
        return this;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeNBT(tagCompound);
        return tagCompound;
    }

    public static Frequency fromNBT(NBTTagCompound tagCompound) {
        NBTTagCompound frequencyTag = tagCompound;
        if (frequencyTag.hasKey("Frequency")) {
            frequencyTag = frequencyTag.getCompoundTag("Frequency");
        }
        return new Frequency().readNBT(frequencyTag);
    }

    public static Frequency fromItemStack(ItemStack stack) {
        Frequency frequency = new Frequency();
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("Frequency")) {
                NBTTagCompound tagCompound = stack.getTagCompound().getCompoundTag("Frequency");
                frequency.setFrequency(fromNBT(tagCompound));
            }
        }
        return frequency;
    }

    @Override
    public String toString() {
        return "left=" + getLeft() + ",middle=" + getMiddle() + ",right=" + getRight();
    }

    @Override
    public int hashCode() {
        return (left + middle * 31) * 31 + right;
    }
}
