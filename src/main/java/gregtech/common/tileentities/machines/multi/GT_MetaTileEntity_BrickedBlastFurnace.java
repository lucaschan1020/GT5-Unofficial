package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_PrimitiveInputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_PrimitiveOutputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_PrimitiveMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.WorldSpawnedEventBuilder;
import gregtech.common.GT_Pollution;
import gregtech.common.gui.GT_Container_PrimitiveBlastFurnace;
import gregtech.common.gui.GT_GUIContainer_PrimitiveBlastFurnace;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import org.lwjgl.input.Keyboard;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.objects.XSTR.XSTR_INSTANCE;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdderOptional;

public class GT_MetaTileEntity_BrickedBlastFurnace extends GT_MetaTileEntity_PrimitiveMultiBlockBase<GT_MetaTileEntity_BrickedBlastFurnace>
        implements ISecondaryDescribable {
    protected static final int TEXTURE_PAGE_INDEX = 0;
    protected static final int CASING_INDEX = 15;
    protected static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_BrickedBlastFurnace> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_BrickedBlastFurnace>builder()
        .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
            {"CCC", "C-C", "CCC"},
            {"CCC", "CLC", "CCC"},
            {"C~C", "CLC", "CCC"},
            {"CCC", "CCC", "CCC"}
        }))
        .addElement('C', ofHatchAdderOptional(GT_MetaTileEntity_BrickedBlastFurnace::addHatch, ((TEXTURE_PAGE_INDEX * 128) + (3 * 16) + CASING_INDEX), 1, GregTech_API.sBlockCasings4, CASING_INDEX))
        .addElement('L', ofChain(isAir(), ofBlock(Blocks.lava, 0), ofBlock(Blocks.flowing_lava, 0)))
        .build();
    private GT_Multiblock_Tooltip_Builder tooltipBuilder;

    public GT_MetaTileEntity_BrickedBlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_BrickedBlastFurnace(String aName) {
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

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        if (tooltipBuilder == null) {
            tooltipBuilder = new GT_Multiblock_Tooltip_Builder();
            tooltipBuilder
                .addMachineType("Blast Furnace")
                .addInfo("Controller Block for the Bricked Blast Furnace")
                .addInfo("Usable for Steel and general Pyrometallurgy")
                .addInfo("Has a useful interface, unlike other gregtech multis")
                .addPollutionAmount(GT_Mod.gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond)
                .addSeparator()
                .beginStructureBlock(3, 4, 3, true)
                .addController("Front center")
                .addOtherStructurePart("Firebricks", "Everything except the controller")
                .addStructureInfo("The top block is also empty")
                .addStructureInfo("You can share the walls of GT multis, so")
                .addStructureInfo("each additional one costs less, up to 4")
                .toolTipFinisher("Gregtech");
        }
        return tooltipBuilder;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_BrickedBlastFurnace> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ITexture[] getTexture(
            IGregTechTileEntity aBaseMetaTileEntity,
            byte aSide,
            byte aFacing,
            byte aColorIndex,
            boolean aActive,
            boolean aRedstone) {
        if (aSide == aFacing) {
            return aActive ?
                new ITexture[]{TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE),
                    TextureFactory.builder().addIcon(BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE_GLOW).glow().build()} :
                new ITexture[]{TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_INACTIVE)};
        }
        return new ITexture[]{TextureFactory.of(Textures.BlockIcons.MACHINE_CASING_DENSEBRICKS)};
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_BrickedBlastFurnace(this.mName);
    }

    protected boolean addHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveInputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27010) {
                addInputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            } else if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_PrimitiveOutputBus && aMetaTileEntity.getBaseMetaTileEntity().getMetaTileID() == 27011) {
                addOutputToMachineList(aTileEntity, aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (checkPiece(STRUCTURE_PIECE_MAIN, 1, 2, 0)) {
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
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 2, 0);
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_PrimitiveBlastFurnace(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_PrimitiveBlastFurnace(aPlayerInventory, aBaseMetaTileEntity, "Bricked Blast Furnace", GT_Recipe.GT_Recipe_Map.sPrimitiveBlastRecipes.mNEIName);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        if (aBaseMetaTileEntity.isClientSide() && aBaseMetaTileEntity.isActive()) {
            new WorldSpawnedEventBuilder.ParticleEventBuilder()
                .setMotion(0D, 0.3D, 0D)
                .setIdentifier("largesmoke")
                .setPosition(
                    aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1) + XSTR_INSTANCE.nextFloat(),
                    aBaseMetaTileEntity.getOffsetY(aBaseMetaTileEntity.getBackFacing(), 1),
                    aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1) + XSTR_INSTANCE.nextFloat()
                )
                .setWorld(getBaseMetaTileEntity().getWorld())
                .run();
        }
        if (aBaseMetaTileEntity.isServerSide()) {
            if (this.mMaxProgresstime > 0 && (aTimer % 20L == 0L)) {
                GT_Pollution.addPollution(this.getBaseMetaTileEntity().getWorld(),
                    new ChunkPosition(this.getBaseMetaTileEntity().getXCoord(), this.getBaseMetaTileEntity().getYCoord(),
                        this.getBaseMetaTileEntity().getZCoord()),
                    GT_Mod.gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond);
            }

            if (aBaseMetaTileEntity.isActive()) {
                if (aBaseMetaTileEntity.getAir(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                    aBaseMetaTileEntity.getYCoord(), aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1))) {
                    aBaseMetaTileEntity.getWorld().setBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                        aBaseMetaTileEntity.getYCoord(), aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1),
                        Blocks.lava, 1, 2);
                    this.mUpdate = 1;
                }
                if (aBaseMetaTileEntity.getAir(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                    aBaseMetaTileEntity.getYCoord() + 1, aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1))) {
                    aBaseMetaTileEntity.getWorld().setBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                        aBaseMetaTileEntity.getYCoord() + 1, aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1),
                        Blocks.lava, 1, 2);
                    this.mUpdate = 1;
                }
            } else {
                if (aBaseMetaTileEntity.getBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                    aBaseMetaTileEntity.getYCoord(),
                    aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1)) == Blocks.lava) {
                    aBaseMetaTileEntity.getWorld().setBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                        aBaseMetaTileEntity.getYCoord(), aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1),
                        Blocks.air, 0, 2);
                    this.mUpdate = 1;
                }
                if (aBaseMetaTileEntity.getBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                    aBaseMetaTileEntity.getYCoord() + 1,
                    aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1)) == Blocks.lava) {
                    aBaseMetaTileEntity.getWorld().setBlock(aBaseMetaTileEntity.getOffsetX(aBaseMetaTileEntity.getBackFacing(), 1),
                        aBaseMetaTileEntity.getYCoord() + 1, aBaseMetaTileEntity.getOffsetZ(aBaseMetaTileEntity.getBackFacing(), 1),
                        Blocks.air, 0, 2);
                    this.mUpdate = 1;
                }
            }
        }
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
        this.mOutputItems = tRecipe.mOutputs.clone();
        this.mOutputFluids = tRecipe.mFluidOutputs.clone();
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
        return GT_Recipe.GT_Recipe_Map.sPrimitiveBlastRecipes;
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return getTooltip().getStructureHint();
    }
}
