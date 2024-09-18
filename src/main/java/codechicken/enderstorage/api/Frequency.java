package codechicken.enderstorage.api;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.util.Copyable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created by covers1624 on 4/26/2016.
 */
public final class Frequency implements Copyable<Frequency> {

    public EnumColour left;
    public EnumColour middle;
    public EnumColour right;
    public @Nullable UUID owner;
    public @Nullable Component ownerName;

    public Frequency() {
        this(EnumColour.WHITE, EnumColour.WHITE, EnumColour.WHITE);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right) {
        this(left, middle, right, null, null);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right, @Nullable UUID owner, @Nullable Component ownerName) {
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.owner = owner;
        this.ownerName = ownerName;
    }

    public Frequency(CompoundTag tagCompound) {
        left = EnumColour.fromWoolMeta(tagCompound.getInt("left"));
        middle = EnumColour.fromWoolMeta(tagCompound.getInt("middle"));
        right = EnumColour.fromWoolMeta(tagCompound.getInt("right"));
        if (tagCompound.hasUUID("owner")) {
            owner = tagCompound.getUUID("owner");
        }
        if (tagCompound.contains("owner_name")) {
            ownerName = Component.Serializer.fromJson(tagCompound.getString("owner_name"));
        }
    }

    public static Frequency fromString(String left, String middle, String right) {
        return fromString(left, middle, right, null, null);
    }

    public static Frequency fromString(String left, String middle, String right, UUID owner, Component ownerName) {
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
        this.left = left;
        return this;
    }

    public Frequency setMiddle(EnumColour middle) {
        this.middle = middle;
        return this;
    }

    public Frequency setRight(EnumColour right) {
        this.right = right;
        return this;
    }

    public Frequency setOwner(Player player) {
        owner = player.getUUID();
        ownerName = player.getName();
        return this;
    }

    public Frequency clearOwner() {
        owner = null;
        ownerName = null;
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
        left = frequency.left;
        middle = frequency.middle;
        right = frequency.right;
        owner = frequency.owner;
        ownerName = frequency.ownerName;
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

    public @Nullable UUID getOwner() {
        return owner;
    }

    public @Nullable Component getOwnerName() {
        return ownerName;
    }

    public EnumColour[] toArray() {
        return new EnumColour[] { left, middle, right };
    }

    private CompoundTag write_internal(CompoundTag tagCompound) {
        tagCompound.putInt("left", left.getWoolMeta());
        tagCompound.putInt("middle", middle.getWoolMeta());
        tagCompound.putInt("right", right.getWoolMeta());
        if (owner != null) {
            tagCompound.putUUID("owner", owner);
        }
        if (ownerName != null) {
            tagCompound.putString("owner_name", Component.Serializer.toJson(ownerName));
        }
        return tagCompound;
    }

    public CompoundTag writeToNBT(CompoundTag tagCompound) {
        write_internal(tagCompound);
        return tagCompound;
    }

    public void writeToPacket(MCDataOutput packet) {
        packet.writeCompoundNBT(write_internal(new CompoundTag()));
    }

    public static Frequency readFromPacket(MCDataInput packet) {
        return new Frequency(packet.readCompoundNBT());
    }

    public static Frequency readFromStack(ItemStack stack) {
        CompoundTag stackTag = stack.getTag();

        if (stackTag != null && stackTag.contains("Frequency")) {
            return new Frequency(stackTag.getCompound("Frequency"));
        }
        return new Frequency();
    }

    public ItemStack writeToStack(ItemStack stack) {
        CompoundTag tagCompound = stack.getOrCreateTag();
        tagCompound.put("Frequency", write_internal(new CompoundTag()));
        return stack;
    }

    @Override
    public String toString() {
        String owner = "";
        if (hasOwner()) {
            owner = ",owner=" + this.owner;
        }
        return "left=" + getLeft().getSerializedName() + ",middle=" + getMiddle().getSerializedName() + ",right=" + getRight().getSerializedName() + owner;
    }

    public Component getTooltip() {
        return Component.translatable(getLeft().getUnlocalizedName())
                .append("/")
                .append(Component.translatable(getMiddle().getUnlocalizedName()))
                .append("/")
                .append(Component.translatable(getRight().getUnlocalizedName()));
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
