//package codechicken.enderstorage.plugin.jei;
//
//import codechicken.enderstorage.api.Frequency;
//import codechicken.enderstorage.recipe.Factories.MultiIngredient;
//import codechicken.enderstorage.recipe.RecipeBase;
//import codechicken.enderstorage.util.LogHelper;
//import codechicken.lib.colour.EnumColour;
//import mezz.jei.api.gui.IGuiItemStackGroup;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.recipe.IFocus;
//import mezz.jei.api.recipe.IFocus.Mode;
//import mezz.jei.api.recipe.IRecipeWrapper;
//import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.util.NonNullList;
//import net.minecraftforge.oredict.OreDictionary;
//import org.apache.logging.log4j.Level;
//
//import java.util.*;
//
///**
// * Created by covers1624 on 8/07/2017.
// */
//public class ESCraftingRecipeWrapper implements IRecipeWrapper, ICustomCraftingRecipeWrapper {
//
//    private final List<ItemStack> output;
//    private final List<List<ItemStack>> inputs;
//    private final RecipeBase recipe;
//    private final int woolIndex;
//    private final Map<EnumColour, List<ItemStack>> woolOres = new HashMap<>();
//
//    public ESCraftingRecipeWrapper(RecipeBase recipe) {
//        this.recipe = recipe;
//
//        ItemStack outputStack = recipe.getRecipeOutput();
//
//        inputs = new LinkedList<>();
//        List<ItemStack> wools = new LinkedList<>();
//        for (EnumColour colour : EnumColour.values()) {
//            List<ItemStack> wool = OreDictionary.getOres(colour.getWoolOreName());
//            wools.addAll(wool);
//            woolOres.put(colour, wool);
//        }
//        NonNullList<Ingredient> ingredients = recipe.getIngredients();
//        int woolIndex = 0;
//        for (int i = 0; i < ingredients.size(); i++) {
//            Ingredient ingredient = ingredients.get(i);
//            List<ItemStack> stacks = new LinkedList<>();
//            if (ingredient instanceof MultiIngredient) {
//                woolIndex = i;
//                stacks.addAll(wools);
//            } else {
//                Collections.addAll(stacks, ingredient.getMatchingStacks());
//            }
//            inputs.add(stacks);
//        }
//        this.woolIndex = woolIndex;
//        output = new LinkedList<>();
//        for (ItemStack stack : wools) {
//            EnumColour colour = EnumColour.fromWoolStack(stack);
//            if (colour != null) {
//                Frequency frequency = new Frequency(colour, colour, colour);
//                output.add(frequency.writeToStack(outputStack.copy()));
//            } else {
//                LogHelper.log(Level.WARN, "Colour is null for known wool stack.. wot. {}", stack);
//            }
//        }
//
//    }
//
//    @Override
//    public void getIngredients(IIngredients ingredients) {
//        ingredients.setInputLists(ItemStack.class, inputs);
//        ingredients.setOutput(ItemStack.class, output);
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
//        List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class).get(0);
//
//        IFocus<?> iFocus = recipeLayout.getFocus();
//        Object focusObject = iFocus.getValue();
//
//        if (focusObject instanceof ItemStack) {
//            ItemStack focus = (ItemStack) focusObject;
//            Mode mode = iFocus.getMode();
//
//            if (mode == Mode.INPUT) {
//                EnumColour woolColour = EnumColour.fromWoolStack(focus);
//                if (woolColour != null) {
//                    Frequency freq = new Frequency(woolColour, woolColour, woolColour);
//                    ItemStack newStack = freq.writeToStack(recipe.getRecipeOutput().copy());
//                    outputs = Collections.singletonList(newStack);
//                }
//            } else {
//                Frequency frequency = Frequency.readFromStack(focus);
//                EnumColour colour = frequency.getLeft();
//                inputs.set(woolIndex, woolOres.get(colour));
//
//            }
//        }
//        EnderStorageJEIPlugin.gridHelper.setInputs(guiItemStacks, inputs, 3, 3);
//        guiItemStacks.set(0, outputs);
//    }
//}
