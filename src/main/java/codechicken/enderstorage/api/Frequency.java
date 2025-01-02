package codechicken.enderstorage.api;

import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by covers1624 on 4/26/2016.
 */
public record Frequency(
        EnumColour left,
        EnumColour middle,
        EnumColour right,
        Optional<UUID> owner,
        Optional<Component> ownerName
) {

    public static final Codec<Frequency> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    EnumColour.CODEC.fieldOf("left").forGetter(Frequency::left),
                    EnumColour.CODEC.fieldOf("middle").forGetter(Frequency::middle),
                    EnumColour.CODEC.fieldOf("right").forGetter(Frequency::right),
                    UUIDUtil.CODEC.optionalFieldOf("owner").forGetter(Frequency::owner),
                    ComponentSerialization.CODEC.optionalFieldOf("ownerName").forGetter(Frequency::ownerName)
            ).apply(builder, Frequency::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Frequency> STREAM_CODEC = StreamCodec.composite(
            EnumColour.STREAM_CODEC, Frequency::left,
            EnumColour.STREAM_CODEC, Frequency::middle,
            EnumColour.STREAM_CODEC, Frequency::right,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), Frequency::owner,
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), Frequency::ownerName,
            Frequency::new
    );

    public Frequency() {
        this(EnumColour.WHITE, EnumColour.WHITE, EnumColour.WHITE);
    }

    public Frequency(EnumColour left, EnumColour middle, EnumColour right) {
        this(left, middle, right, Optional.empty(), Optional.empty());
    }

    @Deprecated
    public Frequency(CompoundTag tagCompound) {
        this(
                EnumColour.fromWoolMeta(tagCompound.getInt("left")),
                EnumColour.fromWoolMeta(tagCompound.getInt("middle")),
                EnumColour.fromWoolMeta(tagCompound.getInt("right")),
                tagCompound.hasUUID("owner") ? Optional.of(tagCompound.getUUID("owner")) : Optional.empty(),
                tagCompound.contains("owner_name") ? Optional.of(Component.Serializer.fromJson(tagCompound.getString("owner_name"), RegistryAccess.EMPTY)) : Optional.empty()
        );
    }

    public Frequency withLeft(@Nullable EnumColour left) {
        if (left != null) {
            return new Frequency(left, middle, right, owner, ownerName);
        }
        return this;
    }

    public Frequency withMiddle(@Nullable EnumColour middle) {
        if (middle != null) {
            return new Frequency(left, middle, right, owner, ownerName);
        }
        return this;
    }

    public Frequency withRight(@Nullable EnumColour right) {
        if (right != null) {
            return new Frequency(left, middle, right, owner, ownerName);
        }
        return this;
    }

    public Frequency withOwner(Player player) {
        return new Frequency(left, middle, right, Optional.of(player.getUUID()), Optional.of(player.getName()));
    }

    public Frequency withoutOwner() {
        return new Frequency(left, middle, right, Optional.empty(), Optional.empty());
    }

    public boolean hasOwner() {
        return owner.isPresent() && ownerName.isPresent();
    }

    public Frequency withColours(@Nullable EnumColour[] colours) {
        return withLeft(colours[0])
                .withMiddle(colours[1])
                .withRight(colours[2]);
    }

    public EnumColour[] toArray() {
        return new EnumColour[] { left, middle, right };
    }

    private CompoundTag write_internal(CompoundTag tagCompound) {
        tagCompound.putInt("left", left.getWoolMeta());
        tagCompound.putInt("middle", middle.getWoolMeta());
        tagCompound.putInt("right", right.getWoolMeta());
        owner.ifPresent(uuid -> tagCompound.putUUID("owner", uuid));
        ownerName.ifPresent(component -> tagCompound.putString("owner_name", Component.Serializer.toJson(component, RegistryAccess.EMPTY)));
        return tagCompound;
    }

    @Deprecated
    public void writeToPacket(MCDataOutput packet) {
        packet.writeCompoundNBT(write_internal(new CompoundTag()));
    }

    @Deprecated
    public static Frequency readFromPacket(MCDataInput packet) {
        return new Frequency(packet.readCompoundNBT());
    }

    @Deprecated // Maybe?
    public static Frequency readFromStack(ItemStack stack) {
        return stack.getOrDefault(EnderStorageModContent.FREQUENCY_DATA_COMPONENT, new Frequency());
    }

    @Deprecated // Maybe?
    public ItemStack writeToStack(ItemStack stack) {
        stack.set(EnderStorageModContent.FREQUENCY_DATA_COMPONENT, this);
        return stack;
    }

    @Override
    public String toString() {
        String owner = "";
        if (hasOwner()) {
            owner = ",owner=" + this.owner;
        }
        return "left=" + left().getSerializedName() + ",middle=" + middle().getSerializedName() + ",right=" + right().getSerializedName() + owner;
    }

    public Component getTooltip() {
        return Component.translatable(left().getUnlocalizedName())
                .append("/")
                .append(Component.translatable(middle().getUnlocalizedName()))
                .append("/")
                .append(Component.translatable(right().getUnlocalizedName()));
    }
}
