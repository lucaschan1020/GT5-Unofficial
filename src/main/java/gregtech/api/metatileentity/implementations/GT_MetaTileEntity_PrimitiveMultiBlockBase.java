package gregtech.api.metatileentity.implementations;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.util.extensions.ArrayExt.withoutNulls;

public abstract class GT_MetaTileEntity_PrimitiveMultiBlockBase<T extends GT_MetaTileEntity_EnhancedMultiBlockBase<T>> extends GT_MetaTileEntity_EnhancedMultiBlockBase<T> implements ISecondaryDescribable {
    public GT_Recipe mLastRecipe;
    public ArrayList<GT_MetaTileEntity_Hatch_PrimitiveSteamInput> mPrimitiveSteamInput = new ArrayList<>();
    public ArrayList<GT_MetaTileEntity_Hatch_PrimitiveInputBus> mPrimitiveInputItems = new ArrayList<>();
    public ArrayList<GT_MetaTileEntity_Hatch_PrimitiveOutputBus> mPrimitiveOutputItems = new ArrayList<>();
    public ArrayList<GT_MetaTileEntity_Hatch_PrimitiveInput> mPrimitiveInputFluids = new ArrayList<>();
    public ArrayList<GT_MetaTileEntity_Hatch_PrimitiveOutput> mPrimitiveOutputFluids = new ArrayList<>();

    public GT_MetaTileEntity_PrimitiveMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_PrimitiveMultiBlockBase(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
        return getCurrentDescription();
    }

    @Override
    public boolean isDisplaySecondaryDescription() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }

    public String[] getPrimaryDescription() {
        return getTooltip().getInformation();
    }

    public String[] getSecondaryDescription() {
        return getTooltip().getStructureInformation();
    }

    protected abstract GT_Multiblock_Tooltip_Builder createTooltip();

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean isElectric() {
        return false;
    }

    @Override
    public boolean isTeleporterCompatible() {
        return false;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowCoverOnSide(byte aSide, GT_ItemStack aCoverID) {
        return (GregTech_API.getCoverBehaviorNew(aCoverID.toStack()).isSimpleCover()) && (super.allowCoverOnSide(aSide, aCoverID));
    }

    @Override
    public int increaseProgress(int aProgress) {
        this.mProgresstime += aProgress;
        return this.mMaxProgresstime - this.mProgresstime;
    }

    @Override
    public void doExplosion(long aExplosionPower) {}

    public void clearHatches() {
        mPrimitiveInputFluids.clear();
        mPrimitiveInputItems.clear();
        mPrimitiveInputFluids.clear();
        mPrimitiveOutputItems.clear();
        super.clearHatches();
    }

    @Override
    public boolean addInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveSteamInput){
            mPrimitiveSteamInput.add((GT_MetaTileEntity_Hatch_PrimitiveSteamInput) aMetaTileEntity);
        }
        else if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInputBus){
            ((GT_MetaTileEntity_Hatch_PrimitiveInputBus) aMetaTileEntity).mRecipeMap = getRecipeMap();
            mPrimitiveInputItems.add((GT_MetaTileEntity_Hatch_PrimitiveInputBus) aMetaTileEntity);
        }
        else if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInput)
        {
            ((GT_MetaTileEntity_Hatch_PrimitiveInput) aMetaTileEntity).mRecipeMap = getRecipeMap();
            mPrimitiveInputFluids.add((GT_MetaTileEntity_Hatch_PrimitiveInput) aMetaTileEntity);
        }

        return true;
    }

    @Override
    public boolean addOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutputBus)
            mPrimitiveOutputItems.add((GT_MetaTileEntity_Hatch_PrimitiveOutputBus) aMetaTileEntity);
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutput)
            mPrimitiveOutputFluids.add((GT_MetaTileEntity_Hatch_PrimitiveOutput) aMetaTileEntity);
        return true;
    }

    @Override
    public ArrayList<ItemStack> getStoredOutputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();
//        for (GT_MetaTileEntity_Hatch_Output tHatch : mOutputHatches) {
//            if (isValidMetaTileEntity(tHatch)) {
//                rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(1));
//            }
//        }
        for (GT_MetaTileEntity_Hatch_PrimitiveOutputBus tHatch : mPrimitiveOutputItems) {
            if (isValidMetaTileEntity(tHatch)) {
                for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
                    rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
                }
            }
        }
        return rList;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluids() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        for (GT_MetaTileEntity_Hatch_PrimitiveInput tHatch : mPrimitiveInputFluids) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch) && tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }
        return rList;
    }

    public ArrayList<FluidStack> getStoredSteamsFromHatch() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        for (GT_MetaTileEntity_Hatch_PrimitiveSteamInput tHatch : mPrimitiveSteamInput) {
            if (isValidMetaTileEntity(tHatch) && tHatch.getFillableStack() != null &&
                tHatch.getFillableStack().getFluid() == Materials.Water.mGas) {
                rList.add(tHatch.getFillableStack());
            }
        }
        return rList;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();
        for (GT_MetaTileEntity_Hatch_PrimitiveInputBus tHatch : mPrimitiveInputItems) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch)) {
                for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
                    if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null)
                        rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
                }
            }
        }
        if(getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName().startsWith("gt.integrated_circuit"))
            rList.add(getStackInSlot(1));
        return rList;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (mEUt < 0) {
            long aSteamVal = (((long) -mEUt * 10000) / Math.max(1000, mEfficiency)) * 2;
            if (!tryConsumeSteam((int) aSteamVal)) {
                stopMachine();
                return false;
            }
        }
        return true;
    }


    @Override
    public void onPostTick(final IGregTechTileEntity aBaseMetaTileEntity, final long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mUpdate == 1 || this.mStartUpCheck == 1) {
                this.mPrimitiveSteamInput.clear();
                this.mPrimitiveInputItems.clear();
                this.mPrimitiveInputFluids.clear();
                this.mPrimitiveOutputItems.clear();
                this.mPrimitiveOutputFluids.clear();
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    public int getMaxParallelRecipes() {
        return 8;
    }

    public int getMaxItemOutputFromRecipe() {
        //unlimited
        return Integer.MAX_VALUE;
    }

    public int getMaxFluidOutputFromRecipe() {
        //unlimited
        return Integer.MAX_VALUE;
    }

    public float getRecipeDurationMultiplier() {
        return 1.5f;
    }

    @Override
    public boolean checkRecipe(ItemStack arg0) {
        ArrayList<ItemStack> tItems = getStoredInputs();
        ArrayList<FluidStack> tFluids = getStoredFluids();
        GT_Recipe.GT_Recipe_Map tMap = this.getRecipeMap();
        if (getMaxParallelRecipes() <= 0) return false;
        if (tMap == null) return false;
        ItemStack[] aItemInputs = tItems.toArray(new ItemStack[0]);
        FluidStack[] aFluidInputs = tFluids.toArray(new FluidStack[0]);

        ArrayList<GT_Parallel_Recipe> tParallelRecipes = new ArrayList<>();
        int tTotalDetectedParallelCount = 0;
        int tHighestRecipeDuration = 0;
        while (tTotalDetectedParallelCount < getMaxParallelRecipes()){
            GT_Recipe tRecipe = tMap.findRecipe(getBaseMetaTileEntity(), mLastRecipe, false, V[1], aFluidInputs, null, aItemInputs);
            if (tRecipe == null) {
                break;
            }
            if (tRecipe.mOutputs == null && tRecipe.mFluidOutputs == null) {
                break;
            }
            tHighestRecipeDuration = Math.max(tHighestRecipeDuration, tRecipe.mDuration);
            GT_Parallel_Recipe tDetectedRecipe = new GT_Parallel_Recipe(tRecipe);
            // Count recipes to do in parallel, consuming input items and fluids
            for (; tTotalDetectedParallelCount < getMaxParallelRecipes(); tTotalDetectedParallelCount++) {
                if (!tRecipe.isRecipeInputEqual(true, aFluidInputs, aItemInputs)) {
                    break;
                }
                tDetectedRecipe.mParallelCount++;
            }
            tParallelRecipes.add(tDetectedRecipe);
            aItemInputs = stripZeroStacks(aItemInputs);
            aFluidInputs = stripZeroStacks(aFluidInputs);
        }

        if (tTotalDetectedParallelCount == 0) return false;
        if (tParallelRecipes.size() == 0) return false;

        // Reset outputs and progress stats
        this.mEUt = 0;
        this.mMaxProgresstime = 0;
        this.mOutputItems = new ItemStack[]{};
        this.mOutputFluids = new FluidStack[]{};
        // Remember last recipe - an optimization for findRecipe()
        this.mLastRecipe = tParallelRecipes.get(tParallelRecipes.size() - 1).mRecipe;

        // -- Try not to fail after this point - inputs have already been consumed! --

        // Convert speed bonus to duration multiplier
        // e.g. 100% speed bonus = 200% speed = 100%/200% = 50% recipe duration.
        // EU discount
        int aEUPercent = 100;
        int aSpeedBonusPercent = 0;
        int aOutputChanceRoll = 10000;
        float tParallelRecipeEUt = 0f;
        for (GT_Parallel_Recipe tParallelRecipe : tParallelRecipes){
            tParallelRecipeEUt += tParallelRecipe.getCumulativeEUt();
        }
        tParallelRecipeEUt = tParallelRecipeEUt * (aEUPercent / 100.0f);
        aSpeedBonusPercent = Math.max(-99, aSpeedBonusPercent);
        float tTimeFactor = 100.0f / (100.0f + aSpeedBonusPercent);
        this.mMaxProgresstime = (int)(tHighestRecipeDuration * tTimeFactor * getRecipeDurationMultiplier());

//        this.mEUt = (int)Math.ceil(tTotalEUt*1.5f);
        this.mEUt = (int)Math.ceil(tParallelRecipeEUt);
        //this.mEUt = (3 * tRecipe.mEUt);

        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        this.mEfficiencyIncrease = 10000;

        if (this.mEUt > 0) {
            this.mEUt = (-this.mEUt);
        }

        this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);

        // Collect fluid outputs
        FluidStack[] tOutputFluids = getOutputFluids(tParallelRecipes, getMaxFluidOutputFromRecipe());

        // Collect output item types
        ItemStack[] tOutputItems = getOutputItems(tParallelRecipes, getMaxItemOutputFromRecipe(), aOutputChanceRoll);

        if (tOutputFluids != null) {
            tOutputFluids = withoutNulls(tOutputFluids, FluidStack[]::new);
            // Strip empty stacks
            tOutputFluids = stripZeroStacks(tOutputFluids);
        }

        if (tOutputItems != null) {
            tOutputItems = withoutNulls(tOutputItems, ItemStack[]::new);
            // Sanitize item stack size, splitting any stacks greater than max stack size
            List<ItemStack> splitStacks = new ArrayList<>();
            for (ItemStack tItem : tOutputItems) {
                while (tItem.getMaxStackSize() < tItem.stackSize) {
                    ItemStack tmp = tItem.copy();
                    tmp.stackSize = tmp.getMaxStackSize();
                    tItem.stackSize = tItem.stackSize - tItem.getMaxStackSize();
                    splitStacks.add(tmp);
                }
            }

            if (splitStacks.size() > 0) {
                ItemStack[] tmp = new ItemStack[splitStacks.size()];
                tmp = splitStacks.toArray(tmp);
                tOutputItems = ArrayUtils.addAll(tOutputItems, tmp);
            }

            // Strip empty stacks
            tOutputItems = stripZeroStacks(tOutputItems);
        }

        // Commit outputs
        this.mOutputItems = tOutputItems;
        this.mOutputFluids = tOutputFluids;
        updateSlots();

        return true;
    }

    public FluidStack[] getOutputFluids(ArrayList<GT_Parallel_Recipe> tParallelRecipes, int aMaxFluidOutputFromRecipe) {
        // Collect fluid outputs
        if (tParallelRecipes == null) return null;
        if (tParallelRecipes.size() == 0) return null;
        ArrayList<FluidStack> tOutputFluids = new ArrayList<>();
        for (GT_Parallel_Recipe tParallelRecipe : tParallelRecipes){
            FluidStack[] tCurrentRecipeOutputFluids = tParallelRecipe.getCumulativeFluidOutputs(aMaxFluidOutputFromRecipe);
            if (tCurrentRecipeOutputFluids == null) continue;
            for (FluidStack tCurrentRecipeOutputFluid : tCurrentRecipeOutputFluids) {
                if (tCurrentRecipeOutputFluid == null) continue;
                FluidStack tFluidExist = tOutputFluids.stream().filter(fluid -> fluid.isFluidEqual(tCurrentRecipeOutputFluid)).findFirst().orElse(null);
                if (tFluidExist == null) {
                    tOutputFluids.add(tCurrentRecipeOutputFluid.copy());
                }
                else {
                    tFluidExist.amount += tCurrentRecipeOutputFluid.amount;
                }
            }
        }
        if (tOutputFluids.size() == 0) return null;
        return tOutputFluids.toArray(new FluidStack[0]);
    }

    public ItemStack[] getOutputItems(ArrayList<GT_Parallel_Recipe> tParallelRecipes, int aMaxItemOutputFromRecipe, int aOutputChanceRoll) {
        // Collect output item types
        if (tParallelRecipes == null) return null;
        if (tParallelRecipes.size() == 0) return null;
        ArrayList<ItemStack> tOutputItems = new ArrayList<>();
        for (GT_Parallel_Recipe tParallelRecipe : tParallelRecipes){
            ItemStack[] tCurrentRecipeOutputItems = tParallelRecipe.getCumulativeOutputsAfterChance(getBaseMetaTileEntity(), aMaxItemOutputFromRecipe, aOutputChanceRoll);
            if (tCurrentRecipeOutputItems == null) continue;
            for (ItemStack tCurrentRecipeOutputItem : tCurrentRecipeOutputItems) {
                ItemStack tItemExist = tOutputItems.stream().filter(item -> item.isItemEqual(tCurrentRecipeOutputItem)).findFirst().orElse(null);
                if (tItemExist == null) {
                    tOutputItems.add(tCurrentRecipeOutputItem.copy());
                }
                else {
                    tItemExist.stackSize += tCurrentRecipeOutputItem.stackSize;
                }
            }
        }
        if (tOutputItems.size() == 0) return null;
        return tOutputItems.toArray(new ItemStack[0]);
    }

    public ItemStack[] stripZeroStacks(ItemStack[] aItems) {
        int j = 0;
        for(int i = 0; i < aItems.length; i++)
        {
            if(aItems[i].stackSize > 0) aItems[j++] = aItems[i];
        }
        ItemStack[] tNonZeroStackItems = new ItemStack[j];
        System.arraycopy(aItems, 0, tNonZeroStackItems, 0, j);
        return tNonZeroStackItems;
    }

    public FluidStack[] stripZeroStacks(FluidStack[] aFluids) {
        int j = 0;
        for(int i = 0; i < aFluids.length; i++)
        {
            if(aFluids[i].amount > 0) aFluids[j++] = aFluids[i];
        }
        FluidStack[] tNonZeroStackFluids = new FluidStack[j];
        System.arraycopy(aFluids, 0, tNonZeroStackFluids, 0, j);
        return tNonZeroStackFluids;
    }

    public int getTotalSteamStored() {
        int aSteam = 0;
        for (FluidStack aFluid : getStoredSteamsFromHatch()) {
            aSteam += aFluid.amount;
        }
        return aSteam;
    }

    public boolean tryConsumeSteam(int aAmount) {
        if (getTotalSteamStored() <= aAmount) {
            return false;
        }
        else {
            return this.depleteSteamHatch(Materials.Water.getGas(aAmount));
        }
    }

    @Override
    public boolean depleteInput(ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack)) return false;
        FluidStack aLiquid = GT_Utility.getFluidForFilledItem(aStack, true);
        if (aLiquid != null) return depleteInput(aLiquid);
        for (GT_MetaTileEntity_Hatch_PrimitiveInput tHatch : mPrimitiveInputFluids) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch)) {
                if (GT_Utility.areStacksEqual(aStack, tHatch.getBaseMetaTileEntity().getStackInSlot(0))) {
                    if (tHatch.getBaseMetaTileEntity().getStackInSlot(0).stackSize >= aStack.stackSize) {
                        tHatch.getBaseMetaTileEntity().decrStackSize(0, aStack.stackSize);
                        return true;
                    }
                }
            }
        }
        for (GT_MetaTileEntity_Hatch_PrimitiveInputBus tHatch : mPrimitiveInputItems) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch)) {
                for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
                    if (GT_Utility.areStacksEqual(aStack, tHatch.getBaseMetaTileEntity().getStackInSlot(i))) {
                        if (tHatch.getBaseMetaTileEntity().getStackInSlot(i).stackSize >= aStack.stackSize) {
                            tHatch.getBaseMetaTileEntity().decrStackSize(i, aStack.stackSize);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean depleteSteamHatch(FluidStack aFluid) {
        if (aFluid == null) return false;
        for (GT_MetaTileEntity_Hatch_PrimitiveSteamInput tHatch : mPrimitiveSteamInput) {
            if (isValidMetaTileEntity(tHatch)) {
                FluidStack tLiquid = tHatch.getFluid();
                if (tLiquid != null && tLiquid.isFluidEqual(aFluid)) {
                    tLiquid = tHatch.drain(aFluid.amount, true);
                    if (tLiquid == null) continue;
                    aFluid.amount -= tLiquid.amount;
                    if (aFluid.amount <= 0) return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        for (GT_MetaTileEntity_Hatch_PrimitiveInput tHatch : mPrimitiveInputFluids) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch)) {
                FluidStack tLiquid = tHatch.getFluid();
                if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                    tLiquid = tHatch.drain(aLiquid.amount, false);
                    if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                        tLiquid = tHatch.drain(aLiquid.amount, true);
                        return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateSlots() {
        for (GT_MetaTileEntity_Hatch_PrimitiveSteamInput tHatch : mPrimitiveSteamInput)
            if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
        for (GT_MetaTileEntity_Hatch_PrimitiveInput tHatch : mPrimitiveInputFluids)
            if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
        for (GT_MetaTileEntity_Hatch_PrimitiveInputBus tHatch : mPrimitiveInputItems)
            if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
    }


    protected static boolean dumpPrimitiveFluid(List<GT_MetaTileEntity_Hatch_PrimitiveOutput> aOutputHatches, FluidStack copiedFluidStack, boolean restrictiveHatchesOnly){
        for (GT_MetaTileEntity_Hatch_PrimitiveOutput tHatch : aOutputHatches) {
            if (!isValidMetaTileEntity(tHatch) || (restrictiveHatchesOnly && tHatch.mMode == 0)) {
                continue;
            }
            if (GT_ModHandler.isSteam(copiedFluidStack)) {
                if (!tHatch.outputsSteam()) {
                    continue;
                }
            } else {
                if (!tHatch.outputsLiquids()) {
                    continue;
                }
                if (tHatch.isFluidLocked() && tHatch.getLockedFluidName() != null && !tHatch.getLockedFluidName().equals(copiedFluidStack.getFluid().getName())) {
                    continue;
                }
            }
            int tAmount = tHatch.fill(copiedFluidStack, false);
            if (tAmount >= copiedFluidStack.amount) {
                boolean filled = tHatch.fill(copiedFluidStack, true) >= copiedFluidStack.amount;
                tHatch.onEmptyingContainerWhenEmpty();
                return filled;
            } else if (tAmount > 0) {
                copiedFluidStack.amount = copiedFluidStack.amount - tHatch.fill(copiedFluidStack, true);
                tHatch.onEmptyingContainerWhenEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean addOutput(ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack)) return false;
        aStack = GT_Utility.copyOrNull(aStack);
        for (GT_MetaTileEntity_Hatch_PrimitiveOutputBus tHatch : mPrimitiveOutputItems) {
            if (isValidMetaTileEntity(tHatch) && tHatch.storeAll(aStack)) {
                return true;
            }
        }
        boolean outputSuccess = true;
        while (outputSuccess && aStack.stackSize > 0) {
            outputSuccess = false;
            ItemStack single = aStack.splitStack(1);
            for (GT_MetaTileEntity_Hatch_PrimitiveOutput tHatch : mPrimitiveOutputFluids) {
                if (!outputSuccess && isValidMetaTileEntity(tHatch) && tHatch.outputsItems()) {
                    if (tHatch.getBaseMetaTileEntity().addStackToSlot(1, single)) outputSuccess = true;
                }
            }
        }
        return outputSuccess;
    }

    @Override
    public boolean addOutput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        FluidStack copiedFluidStack = aLiquid.copy();
        if (!dumpPrimitiveFluid(mPrimitiveOutputFluids, copiedFluidStack, true)){
            dumpPrimitiveFluid(mPrimitiveOutputFluids, copiedFluidStack, false);
        }
        return false;
    }

    @Override
    protected void addFluidOutputs(FluidStack[] mOutputFluids2) {
        for (FluidStack outputFluidStack : mOutputFluids2) {
            addOutput(outputFluidStack);
        }
    }
}
