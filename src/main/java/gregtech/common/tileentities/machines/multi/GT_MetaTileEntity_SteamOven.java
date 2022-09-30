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
import gregtech.api.metatileentity.implementations.*;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import net.minecraft.item.ItemStack;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdderOptional;

public class GT_MetaTileEntity_SteamOven extends GT_MetaTileEntity_PrimitiveMultiBlockBase<GT_MetaTileEntity_SteamOven> implements ISecondaryDescribable {
    protected static final int TEXTURE_PAGE_INDEX = 0;
    protected static final int CASING_INDEX = 10;
    protected static final int PIPE_INDEX = 45;
    protected static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_SteamOven> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_SteamOven>builder()
        .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
            {" C ", " C ", " C "},
            {"C~C", "C-C", "CCC"},
            {"FFF", "FFF", "FFF"}
        }))
        .addElement('C', ofHatchAdderOptional(GT_MetaTileEntity_SteamOven::addIOHatch, (TEXTURE_PAGE_INDEX * 128) + CASING_INDEX, 1, GregTech_API.sBlockCasings1, 10))
        .addElement('F', ofHatchAdderOptional(GT_MetaTileEntity_SteamOven::addSteamHatch, (TEXTURE_PAGE_INDEX * 128) + PIPE_INDEX, 1, GregTech_API.sBlockCasings3, 13))
        .build();

    public GT_MetaTileEntity_SteamOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_SteamOven(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Furnace")
            .addInfo("Controller block for the Steam Oven")
            .addInfo("Multi furnace steam powered HAYO!")
            .addPollutionAmount(GT_Mod.gregtechproxy.mPollutionSteamOvenPerSecond)
            .addSeparator()
            .beginStructureBlock(3, 3, 3, true)
            .addController("Front center")
            .addCasingInfo("Bronze Plated Bricks on top 2 layers", 0)
            .addCasingInfo("Bronze Firebox Casing on bottom layer", 0)
            .addInputBus("Any Input Bus (Steam only)", 1)
            .addOutputBus("Any Output Bus (Steam only)", 1)
            .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                    casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_STEAM_FURNACE_ACTIVE).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_STEAM_FURNACE_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX],
                TextureFactory.builder().addIcon(OVERLAY_FRONT_STEAM_FURNACE).extFacing().build(),
                TextureFactory.builder().addIcon(OVERLAY_FRONT_STEAM_FURNACE_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{casingTexturePages[TEXTURE_PAGE_INDEX][CASING_INDEX]};
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_SteamOven(this.mName);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 1, 0);
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
    public IStructureDefinition<GT_MetaTileEntity_SteamOven> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    protected boolean addIOHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if ((aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27003) ||
                (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInput && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27001) ||
                aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveSteamInput) {
                addInputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            } else if ((aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27004) ||
                (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutput && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27002)) {
                addOutputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    protected boolean addSteamHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveSteamInput) {
                addInputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sFurnaceRecipes;
    }
}
