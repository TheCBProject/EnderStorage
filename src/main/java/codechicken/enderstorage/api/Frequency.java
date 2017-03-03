package codechicken.enderstorage.api;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.util.Copyable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

/**
 * Created by covers1624 on 4/26/2016.
 */
public final class Frequency implements Copyable<Frequency> {

    public int left;
    public int middle;
    public int right;
    public String owner;

    public Frequency() {

        this(0, 0, 0, null);
    }

    public Frequency(int left, int middle, int right, String owner) {

        this.left = left;
        this.middle = middle;
        this.right = right;
        this.owner = owner;
    }

    public Frequency(int left, int middle, int right) {

        this(left, middle, right, null);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right, String owner) {

        this(left.ordinal(), middle.ordinal(), right.ordinal(), owner);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right) {

        this(left, middle, right, null);
    }

    public static Frequency fromString(String left, String middle, String right, String owner) {

        EnumColour c1 = EnumColour.fromName(left);
        EnumColour c2 = EnumColour.fromName(middle);
        EnumColour c3 = EnumColour.fromName(right);
        if (c1 == null) {
            throw new RuntimeException(left + " is an invalid colour!");
        }
        if (c2 == null) {
            throw new RuntimeException(middle + " is an invalid colour!");
        }
        if (c3 == null) {
            throw new RuntimeException(right + " is an invalid colour!");
        }
        return new Frequency(c1, c2, c3, owner);
    }

    public static Frequency fromString(String left, String middle, String right) {

        return fromString(left, middle, right, null);
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

    public Frequency setOwner(String owner) {

        this.owner = owner;
        return this;
    }

    public boolean hasOwner() {

        return owner != null;
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
        setOwner(frequency.owner);
        return this;
    }

    public String getLeft() {

        return EnumColour.values()[left].getMinecraftName();
    }

    public String getMiddle() {

        return EnumColour.values()[middle].getMinecraftName();
    }

    public String getRight() {

        return EnumColour.values()[right].getMinecraftName();
    }

    public EnumColour getLeftRaw() {

        return EnumColour.values()[left];
    }

    public EnumColour getMiddleRaw() {

        return EnumColour.values()[middle];
    }

    public EnumColour getRightRaw() {

        return EnumColour.values()[right];
    }

    public String getLocalizedLeft() {

        return I18n.translateToLocal(getLeftRaw().getUnlocalizedName());
    }

    public String getLocalizedMiddle() {

        return I18n.translateToLocal(getMiddleRaw().getUnlocalizedName());
    }

    public String getLocalizedRight() {

        return I18n.translateToLocal(getRightRaw().getUnlocalizedName());
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
        if (tagCompound.hasKey("owner")) {
            owner = tagCompound.getString("owner");
        }
        return this;
    }

    public Frequency writeNBT(NBTTagCompound tagCompound) {

        tagCompound.setInteger("left", left);
        tagCompound.setInteger("middle", middle);
        tagCompound.setInteger("right", right);
        if (owner != null) {
            tagCompound.setString("owner", owner);
        }
        return this;
    }

    public NBTTagCompound toNBT() {

        NBTTagCompound tagCompound = new NBTTagCompound();
        writeNBT(tagCompound);
        return tagCompound;
    }

    public static Frequency fromNBT(NBTTagCompound tagCompound) {

        NBTTagCompound frequencyTag = tagCompound;
        if (tagCompound.hasKey("Frequency")) {
            frequencyTag = tagCompound.getCompoundTag("Frequency");
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

    public static ItemStack toItemStack(ItemStack stack, Frequency frequency) {

        return frequency.toItemStack(stack);
    }

    public ItemStack toItemStack(ItemStack stack) {

        NBTTagCompound tagCompound = new NBTTagCompound();
        if (stack.hasTagCompound()) {
            tagCompound = stack.getTagCompound();
        }
        NBTTagCompound frequencyTag = new NBTTagCompound();
        writeNBT(frequencyTag);
        tagCompound.setTag("Frequency", frequencyTag);
        stack.setTagCompound(tagCompound);
        return stack;
    }

    public String toModelLoc() {

        return "left=" + getLeft() + ",middle=" + getMiddle() + ",right=" + getRight() + ",owned=" + hasOwner();
    }

    @Override
    public String toString() {

        String owner = "";
        if (hasOwner()) {
            owner = ",owner=" + this.owner;
        }
        return "left=" + getLeft() + ",middle=" + getMiddle() + ",right=" + getRight() + owner;
    }

    @Override
    public int hashCode() {

        return toString().hashCode();
    }

    @Override
    public Frequency copy() {

        return new Frequency(this.left, this.middle, this.right, this.owner);
    }
}
