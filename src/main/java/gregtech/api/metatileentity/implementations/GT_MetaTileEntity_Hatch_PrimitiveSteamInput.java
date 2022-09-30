package gregtech.api.metatileentity.implementations;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PUMP;

public class GT_MetaTileEntity_Hatch_PrimitiveSteamInput extends GT_MetaTileEntity_Hatch {
    public final Fluid mLockedFluid = Materials.Water.mGas;

    public GT_MetaTileEntity_Hatch_PrimitiveSteamInput(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 3, new String[]{
            "Steam Hatch for Primitive Multiblocks",
            "Capacity: " + 64000 + "L"});
    }

    public GT_MetaTileEntity_Hatch_PrimitiveSteamInput(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Hatch_PrimitiveSteamInput(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PUMP)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PUMP)};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_PrimitiveSteamInput(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public boolean doesFillContainers() {
        //return true;
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return true;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public boolean displaysItemStack() {
        return true;
    }

    @Override
    public boolean displaysStackSize() {
        return false;
    }

    public void updateSlots() {
        if (mInventory[getInputSlot()] != null && mInventory[getInputSlot()].stackSize <= 0)
            mInventory[getInputSlot()] = null;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return aFluid.getFluid() == this.mLockedFluid;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        if (aSide == aBaseMetaTileEntity.getFrontFacing() && aIndex == 0) {
            FluidStack fs = GT_Utility.getFluidForFilledItem(aStack, true);
            return fs != null && fs.getFluid() == this.mLockedFluid;
        }
        return false;
    }

    @Override
    public int getCapacity() {
        return 64000;
    }

    @Override
    public int getTankPressure() {
        return -100;
    }
}
