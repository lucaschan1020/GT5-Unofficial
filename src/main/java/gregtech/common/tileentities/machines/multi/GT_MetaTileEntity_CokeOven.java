package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_PrimitiveInputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_PrimitiveOutput;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_PrimitiveOutputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_PrimitiveMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.common.gui.GT_GUIContainer_CokeOven;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdderOptional;

public class GT_MetaTileEntity_CokeOven extends GT_MetaTileEntity_PrimitiveMultiBlockBase<GT_MetaTileEntity_CokeOven> implements ISecondaryDescribable {

    protected static final int TEXTURE_PAGE_INDEX = 7;
    protected static final int CASING_INDEX = 0;
    protected static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_CokeOven> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_CokeOven>builder()
        .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
            {"CCC", "CCC", "CCC"},
            {"C~C", "C-C", "CCC"},
            {"CCC", "CCC", "CCC"}
        }))
        .addElement('C', ofHatchAdderOptional(GT_MetaTileEntity_CokeOven::addHatch, (TEXTURE_PAGE_INDEX * 128) + CASING_INDEX, 1, GregTech_API.sBlockCasingsCustom, CASING_INDEX))
        .build();

    public GT_MetaTileEntity_CokeOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_CokeOven(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Coke Oven")
            .addInfo("Controller block for the Coke Oven")
            .addInfo("More coal coke and charcoal!")
            .addPollutionAmount(GT_Mod.gregtechproxy.mPollutionCokeOvenPerSecond)
            .addSeparator()
            .beginStructureBlock(3, 3, 3, true)
            .addController("Front center")
            .addCasingInfo("Coke Oven Brick", 0)
            .addInputBus("Any Coke Oven Brick", 1)
            .addOutputBus("Any Coke Oven Brick", 1)
            .addOutputHatch("Any Coke Oven Brick", 1)
            .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                    casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_COKE_OVEN_ACTIVE).extFacing().build(),
                    TextureFactory.builder().addIcon(MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX],
                TextureFactory.builder().addIcon(OVERLAY_FRONT_COKE_OVEN).extFacing().build()};
        }
        return new ITexture[]{casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX]};
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_CokeOven(this.mName);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 1, 0);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_CokeOven(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "CokeOven.png");
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (checkPiece(STRUCTURE_PIECE_MAIN, 1, 1, 0)) {
            mWrench = true;
            mScrewdriver = true;
            mSoftHammer = true;
            mHardHammer = true;
            mSolderingTool = true;
            mCrowbar = true;
            return true;
        }
        return false;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_CokeOven> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    protected boolean addHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27007) {
                addInputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            } else if ((aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27008) ||
                (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutput && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27006)) {
                addOutputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        return processRecipe(getCompactedInputs());
    }

    protected boolean processRecipe(ItemStack[] tItems) {
        if (tItems.length <= 0)
            return false;

        GT_Recipe tRecipe = getRecipeMap().findRecipe(getBaseMetaTileEntity(), false, 0, null, tItems);

        if (tRecipe == null)
            return false;

        if (!tRecipe.isRecipeInputEqual(true, null, tItems))
            return false;

        this.mMaxProgresstime = tRecipe.mDuration;
        this.mEfficiency = 10000;
        this.mOutputItems = new ItemStack[]{
            tRecipe.getOutput(0)
        };
        this.mOutputFluids = new FluidStack[]{
            tRecipe.getFluidOutput(0)
        };
        updateSlots();
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public float getRecipeDurationMultiplier() {
        return 1f;
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sCokeOvenRecipes;
    }
}
