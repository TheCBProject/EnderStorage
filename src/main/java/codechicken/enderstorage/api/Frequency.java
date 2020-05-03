package codechicken.enderstorage.api;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.util.Copyable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

/**
 * Created by covers1624 on 4/26/2016.
 */
public final class Frequency implements Copyable<Frequency> {

    public EnumColour left;
    public EnumColour middle;
    public EnumColour right;
    public UUID owner;
    public ITextComponent ownerName;

    public Frequency() {
        this(EnumColour.WHITE, EnumColour.WHITE, EnumColour.WHITE);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right) {
        this(left, middle, right, null, null);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right, UUID owner, ITextComponent ownerName) {
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.owner = owner;
        this.ownerName = ownerName;
    }

    public Frequency(CompoundNBT tagCompound) {
        read_internal(tagCompound);
    }

    public static Frequency fromString(String left, String middle, String right) {
        return fromString(left, middle, right, null, null);
    }

    public static Frequency fromString(String left, String middle, String right, UUID owner, ITextComponent ownerName) {
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
        return new Frequency(c1, c2, c3, owner, ownerName);
    }

    public Frequency setLeft(EnumColour left) {
        if (left != null) {
            this.left = left;
        }
        return this;
    }

    public Frequency setMiddle(EnumColour middle) {
        if (middle != null) {
            this.middle = middle;
        }
        return this;
    }

    public Frequency setRight(EnumColour right) {
        if (right != null) {
            this.right = right;
        }
        return this;
    }

    public Frequency setOwner(UUID owner) {
        this.owner = owner;
        return this;
    }

    public Frequency setOwnerName(ITextComponent ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public boolean hasOwner() {
        return owner != null && ownerName != null;
    }

    public Frequency set(EnumColour[] colours) {
        setLeft(colours[0]);
        setMiddle(colours[1]);
        setRight(colours[2]);
        return this;
    }

    public Frequency set(Frequency frequency) {
        setLeft(frequency.left);
        setMiddle(frequency.middle);
        setRight(frequency.right);
        setOwner(frequency.owner);
        setOwnerName(frequency.ownerName);
        return this;
    }

    public EnumColour getLeft() {
        return left;
    }

    public EnumColour getMiddle() {
        return middle;
    }

    public EnumColour getRight() {
        return right;
    }

    public UUID getOwner() {
        return owner;
    }

    public ITextComponent getOwnerName() {
        return ownerName;
    }

    public EnumColour[] toArray() {
        return new EnumColour[] { left, middle, right };
    }

    protected Frequency read_internal(CompoundNBT tagCompound) {
        left = EnumColour.fromWoolMeta(tagCompound.getInt("left"));
        middle = EnumColour.fromWoolMeta(tagCompound.getInt("middle"));
        right = EnumColour.fromWoolMeta(tagCompound.getInt("right"));
        if (tagCompound.hasUniqueId("owner")) {
            owner = tagCompound.getUniqueId("owner");
        }
        if (tagCompound.contains("owner_name")) {
            ownerName = ITextComponent.Serializer.fromJson(tagCompound.getString("owner_name"));
        }
        return this;
    }

    protected CompoundNBT write_internal(CompoundNBT tagCompound) {
        tagCompound.putInt("left", left.getWoolMeta());
        tagCompound.putInt("middle", middle.getWoolMeta());
        tagCompound.putInt("right", right.getWoolMeta());
        if (owner != null) {
            tagCompound.putUniqueId("owner", owner);
        }
        if (ownerName != null) {
            tagCompound.putString("owner_name", ITextComponent.Serializer.toJson(ownerName));
        }
        return tagCompound;
    }

    public CompoundNBT writeToNBT(CompoundNBT tagCompound) {
        write_internal(tagCompound);
        return tagCompound;
    }

    public void writeToPacket(MCDataOutput packet) {
        packet.writeCompoundNBT(write_internal(new CompoundNBT()));
    }

    public static Frequency readFromPacket(MCDataInput packet) {
        return new Frequency(packet.readCompoundNBT());
    }

    public static Frequency readFromStack(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT stackTag = stack.getTag();
            if (stackTag.contains("Frequency")) {
                return new Frequency(stackTag.getCompound("Frequency"));
            }
        }
        return new Frequency();
    }

    public ItemStack writeToStack(ItemStack stack) {
        CompoundNBT tagCompound = stack.getOrCreateTag();
        tagCompound.put("Frequency", write_internal(new CompoundNBT()));
        return stack;
    }

    public String toModelLoc() {
        return "left=" + getLeft().getName() + ",middle=" + getMiddle().getName() + ",right=" + getRight().getName() + ",owned=" + hasOwner();
    }

    @Override
    public String toString() {
        String owner = "";
        if (hasOwner()) {
            owner = ",owner=" + this.owner;
        }
        return "left=" + getLeft().getName() + ",middle=" + getMiddle().getName() + ",right=" + getRight().getName() + owner;
    }

    public ITextComponent getTooltip() {
        return new TranslationTextComponent(getLeft().getUnlocalizedName())//
                .appendText("/")//
                .appendSibling(new TranslationTextComponent(getMiddle().getUnlocalizedName()))//
                .appendText("/")//
                .appendSibling(new TranslationTextComponent(getRight().getUnlocalizedName()));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public Frequency copy() {
        return new Frequency(this.left, this.middle, this.right, this.owner, this.ownerName);
    }
}
