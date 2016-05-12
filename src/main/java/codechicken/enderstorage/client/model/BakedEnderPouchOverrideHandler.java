package codechicken.enderstorage.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class BakedEnderPouchOverrideHandler extends ItemOverrideList {

    public static final BakedEnderPouchOverrideHandler INSTANCE = new BakedEnderPouchOverrideHandler();

    public BakedEnderPouchOverrideHandler() {
        super(ImmutableList.<ItemOverride>of());
    }
}
