package gregtech.api.util;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class GT_Parallel_Recipe {
    public int mParallelCount = 0;
    public GT_Recipe mRecipe;
    public GT_Parallel_Recipe(GT_Recipe aRecipe){ this.mRecipe = aRecipe; }

    public int getCumulativeEUt(){
        return mRecipe.mEUt * mParallelCount;
    }

    public ItemStack[] getCumulativeOutputs() {
        return getCumulativeOutputs(Integer.MAX_VALUE);
    }

    public ItemStack[] getCumulativeOutputs(int aMaxItemOutputFromRecipe) {
        if (aMaxItemOutputFromRecipe <= 0) return null;
        if (mRecipe.mOutputs == null) return null;
        if (mRecipe.mOutputs.length == 0) return null;
        int tOutputCount = Math.min(aMaxItemOutputFromRecipe, mRecipe.mOutputs.length);
        ItemStack[] tCumulativeOutputs = new ItemStack[tOutputCount];
        for (int i = 0; i < tOutputCount; i++) {
            if (mRecipe.mOutputs[i] != null) {
                tCumulativeOutputs[i] = mRecipe.mOutputs[i].copy();
                tCumulativeOutputs[i].stackSize = mRecipe.mOutputs[i].stackSize * mParallelCount;
            }
            else {
                tCumulativeOutputs[i] = null;
            }
        }
        return tCumulativeOutputs;
    }

    public FluidStack[] getCumulativeFluidOutputs() {
        return getCumulativeFluidOutputs(Integer.MAX_VALUE);
    }

    public FluidStack[] getCumulativeFluidOutputs(int aMaxFluidOutputFromRecipe) {
        if (aMaxFluidOutputFromRecipe <= 0) return null;
        if (mRecipe.mFluidOutputs == null) return null;
        if (mRecipe.mFluidOutputs.length == 0) return null;
        int tOutputCount = Math.min(aMaxFluidOutputFromRecipe, mRecipe.mFluidOutputs.length);
        FluidStack[] tCumulativeFluidOutputs = new FluidStack[tOutputCount];
        for (int i = 0; i < tOutputCount; i++) {
            if (mRecipe.mFluidOutputs[i] != null) {
                tCumulativeFluidOutputs[i] = mRecipe.mFluidOutputs[i].copy();
                tCumulativeFluidOutputs[i].amount = mRecipe.mFluidOutputs[i].amount * mParallelCount;
            }
            else {
                tCumulativeFluidOutputs[i] = null;
            }
        }
        return tCumulativeFluidOutputs;
    }

    /**
     * Taking output chance into account
     */
    public ItemStack[] getCumulativeOutputsAfterChance(IGregTechTileEntity aBaseMetaTileEntity) {
        return getCumulativeOutputsAfterChance(aBaseMetaTileEntity, Integer.MAX_VALUE, 10000);
    }

    /**
     * Taking output chance into account
     */
    public ItemStack[] getCumulativeOutputsAfterChance(IGregTechTileEntity aBaseMetaTileEntity, int aMaxItemOutputFromRecipe, int aOutputChanceRoll) {
        if (aMaxItemOutputFromRecipe <= 0) return null;
        if (mRecipe.mOutputs == null) return null;
        if (mRecipe.mOutputs.length == 0) return null;
        int tOutputCount = Math.min(aMaxItemOutputFromRecipe, mRecipe.mOutputs.length);
        ItemStack[] tCumulativeOutputs = new ItemStack[tOutputCount];
        for (int i = 0; i < tOutputCount; i++) {
            if (mRecipe.mOutputs[i] != null) {
                tCumulativeOutputs[i] = mRecipe.mOutputs[i].copy();
                int tStackSizeIfSuccess = tCumulativeOutputs[i].stackSize;
                tCumulativeOutputs[i].stackSize = 0;
                for (int j = 0; j < mParallelCount; j++) {
                    if (aBaseMetaTileEntity.getRandomNumber(aOutputChanceRoll) < mRecipe.getOutputChance(i))
                        tCumulativeOutputs[i].stackSize += tStackSizeIfSuccess;
                }
            }
            else {
                tCumulativeOutputs[i] = null;
            }
        }
        return tCumulativeOutputs;
    }
}
