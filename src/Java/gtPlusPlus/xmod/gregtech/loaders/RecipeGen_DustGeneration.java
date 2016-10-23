package gtPlusPlus.xmod.gregtech.loaders;

import gregtech.api.enums.GT_Values;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.item.UtilsItems;
import gtPlusPlus.core.util.recipe.UtilsRecipe;
import net.minecraft.item.ItemStack;

public class RecipeGen_DustGeneration {

	public static void generateRecipes(Material material){
		int tVoltageMultiplier = material.getMeltingPoint_K() >= 2800 ? 64 : 16;

		Utils.LOG_INFO("Generating Shaped Crafting recipes for "+material.getLocalizedName()); //TODO
		//Ring Recipe

		if (UtilsRecipe.addShapedGregtechRecipe(
				"craftingToolWrench", null, null,
				null, material.getRod(1), null,
				null, null, null,
				material.getRing(1))){
			Utils.LOG_INFO("Ring Recipe: "+material.getLocalizedName()+" - Success");
		}
		else {
			Utils.LOG_INFO("Ring Recipe: "+material.getLocalizedName()+" - Failed");			
		}


		ItemStack normalDust = material.getDust(1);
		ItemStack smallDust = material.getSmallDust(1);
		ItemStack tinyDust = material.getTinyDust(1);

		ItemStack[] inputStacks = material.getMaterialComposites();
		ItemStack outputStacks = material.getDust(material.smallestStackSizeWhenProcessing);

		if (UtilsRecipe.recipeBuilder(
				tinyDust,	tinyDust, tinyDust, 
				tinyDust, tinyDust, tinyDust, 
				tinyDust, tinyDust, tinyDust,
				normalDust)){
			Utils.LOG_INFO("9 Tiny dust to 1 Dust Recipe: "+material.getLocalizedName()+" - Success");
		}
		else {
			Utils.LOG_INFO("9 Tiny dust to 1 Dust Recipe: "+material.getLocalizedName()+" - Failed");			
		}

		if (UtilsRecipe.recipeBuilder(
				normalDust, null, null, 
				null, null, null, 
				null, null, null,
				material.getTinyDust(9))){
			Utils.LOG_INFO("9 Tiny dust from 1 Recipe: "+material.getLocalizedName()+" - Success");
		}
		else {
			Utils.LOG_INFO("9 Tiny dust from 1 Recipe: "+material.getLocalizedName()+" - Failed");			
		}


		if (UtilsRecipe.recipeBuilder(
				smallDust, smallDust, null, 
				smallDust, smallDust, null, 
				null, null, null,
				normalDust)){
			Utils.LOG_INFO("4 Small dust to 1 Dust Recipe: "+material.getLocalizedName()+" - Success");
		}
		else {
			Utils.LOG_INFO("4 Small dust to 1 Dust Recipe: "+material.getLocalizedName()+" - Failed");			
		}


		if (UtilsRecipe.recipeBuilder(
				null, normalDust, null, 
				null, null, null, 
				null, null, null,
				material.getSmallDust(4))){
			Utils.LOG_INFO("4 Small dust from 1 Dust Recipe: "+material.getLocalizedName()+" - Success");
		}
		else {
			Utils.LOG_INFO("4 Small dust from 1 Dust Recipe: "+material.getLocalizedName()+" - Failed");			
		}


		if (inputStacks.length > 0){
			Utils.LOG_INFO(UtilsItems.getArrayStackNames(inputStacks));
			long[] inputStackSize = material.vSmallestRatio;
			if (inputStackSize != null){
				for (short x=0;x<inputStacks.length;x++){
					if (inputStacks[x] != null && inputStackSize[x] != 0)
					inputStacks[x].stackSize = (int) inputStackSize[x];
				}
				Utils.LOG_INFO(UtilsItems.getArrayStackNames(inputStacks));			
				if (GT_Values.RA.addMixerRecipe(
						inputStacks[0], inputStacks[1],
						inputStacks[2], inputStacks[3],
						null, null,
						outputStacks,
						(int) Math.max(material.getMass() * 2L * 1, 1),
						6 * material.vVoltageMultiplier)){
					Utils.LOG_INFO("Dust Mixer Recipe: "+material.getLocalizedName()+" - Success");
				}
				else {
					Utils.LOG_INFO("Dust Mixer Recipe: "+material.getLocalizedName()+" - Failed");			
				}
			}
		}
	}
}

