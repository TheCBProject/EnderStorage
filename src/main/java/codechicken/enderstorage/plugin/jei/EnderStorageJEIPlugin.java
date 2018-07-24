package codechicken.enderstorage.plugin.jei;

import codechicken.enderstorage.recipe.Factories;
import codechicken.enderstorage.recipe.RecipeBase;
import com.google.common.collect.Sets;
import mezz.jei.api.*;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by covers1624 on 8/07/2017.
 */
@JEIPlugin//TODO FIXME TODO, Recipe handler for recoloring.
public class EnderStorageJEIPlugin implements IModPlugin {

    public static ICraftingGridHelper gridHelper;

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelpers = helpers.getGuiHelper();

        gridHelper = guiHelpers.createCraftingGridHelper(1, 0);
        registry.handleRecipes(Factories.CraftingRecipe.class, ESCraftingRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
    }

}
