package gregtech.common.tileentities.storage;

import gregtech.api.gui.GT_Container_StorageTank;
import gregtech.api.gui.widgets.GT_GUIContainer_StorageTank;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.FLUID_OUT_SIGN;

public class GT_MetaTileEntity_QuantumTank extends GT_MetaTileEntity_BasicTank {

    private boolean voidBreak;
    public boolean mOutputFluid = false;
    public boolean mLockFluid = false;
    public boolean mVoidPartial = false;
    private String mLockedFluidName = null;

    public GT_MetaTileEntity_QuantumTank(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 3, new String[]{
                "Stores " + GT_Utility.formatNumbers(commonSizeCompute(aTier)) + "L of fluid",
                "Can keep its contents when harvested",
                "Sneak when harvesting to void its contents"
        });
    }

    private static int commonSizeCompute(int tier) {
        switch (tier) {
            case 6:
                return 128000000;
            case 7:
                return 256000000;
            case 8:
                return 512000000;
            case 9:
                return 1024000000;
            case 10:
                return 2147483640;
            default:
                return 0;
        }
    }

    public GT_MetaTileEntity_QuantumTank(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    public GT_MetaTileEntity_QuantumTank(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return new ITexture[0][0][0];
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        if (!voidBreak) {
            if (mFluid != null && mFluid.amount > 0)
                aNBT.setTag("mFluid", mFluid.writeToNBT(new NBTTagCompound()));
        }
        super.setItemNBT(aNBT);
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean doesFillContainers() {
        return true;
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

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_StorageTank(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_StorageTank(aPlayerInventory, aBaseMetaTileEntity, getLocalName());
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("OutputFluid", this.mOutputFluid);
        aNBT.setBoolean("LockFluid", this.mLockFluid);
        aNBT.setBoolean("VoidPartial", this.mVoidPartial);
        if (mLockedFluidName != null && mLockedFluidName.length() != 0) aNBT.setString("LockedFluidName", this.mLockedFluidName);
        else aNBT.removeTag("LockedFluidName");
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.mOutputFluid    = aNBT.getBoolean("OutputFluid");
        this.mLockFluid = aNBT.getBoolean("LockFluid");
        this.mVoidPartial = aNBT.getBoolean("VoidPartial");
        this.mLockedFluidName = aNBT.getString("LockedFluidName");
        this.mLockedFluidName = mLockedFluidName.length() == 0 ? null : mLockedFluidName;
    }

    public void setLockedFluidName(String aLockedFluidName) {
        this.mLockedFluidName = aLockedFluidName;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_QuantumTank(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return !mLockFluid || mLockedFluidName == null || mLockedFluidName.equals(aFluid.getUnlocalizedName());
    }

    @Override
    public void onEmptyingContainerWhenEmpty() {
        if (mLockedFluidName == null && mFluid != null) {
            mLockedFluidName = mFluid.getUnlocalizedName();
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{
                MACHINE_CASINGS[mTier][aColorIndex + 1],
                TextureFactory.of(OVERLAY_PIPE_OUT),
                TextureFactory.of(FLUID_OUT_SIGN)
            };
        }
        if (aSide != ForgeDirection.UP.ordinal()) return new ITexture[]{MACHINE_CASINGS[mTier][aColorIndex + 1]};
        return new ITexture[]{
                MACHINE_CASINGS[mTier][aColorIndex + 1],
                TextureFactory.of(OVERLAY_QTANK),
                TextureFactory.builder().addIcon(OVERLAY_QTANK_GLOW).glow().build()
        };
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!aBaseMetaTileEntity.isClientSide()) aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        voidBreak = aPlayer.isSneaking();
        super.onLeftclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public final byte getUpdateData() {
        return 0x00;
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
    public int getTankPressure() {
        return 100;
    }

    @Override
    public int getCapacity() {
        return commonSizeCompute(mTier);
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {

        if (mFluid == null) {
            return new String[]{
                    EnumChatFormatting.BLUE + "Quantum Tank" + EnumChatFormatting.RESET,
                    "Stored Fluid:",
                    EnumChatFormatting.GOLD + "No Fluid" + EnumChatFormatting.RESET,
                    EnumChatFormatting.GREEN + Integer.toString(0) + " L" + EnumChatFormatting.RESET + " " +
                            EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(getCapacity()) + " L" + EnumChatFormatting.RESET
            };
        }
        return new String[]{
                EnumChatFormatting.BLUE + "Quantum Tank" + EnumChatFormatting.RESET,
                "Stored Fluid:",
                EnumChatFormatting.GOLD + mFluid.getLocalizedName() + EnumChatFormatting.RESET,
                EnumChatFormatting.GREEN + GT_Utility.formatNumbers(mFluid.amount) + " L" + EnumChatFormatting.RESET + " " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(getCapacity()) + " L" + EnumChatFormatting.RESET
        };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (this.getBaseMetaTileEntity().isServerSide() && (aTick & 0x7) == 0) {
            if (mVoidPartial && mFluid != null && mFluid.amount >= getCapacity()) {
                mFluid.amount  = getCapacity() - (getCapacity() * 10 / 100);
            }

            IFluidHandler tTileEntity = aBaseMetaTileEntity.getITankContainerAtSide(aBaseMetaTileEntity.getFrontFacing());
            if (tTileEntity == null) return;

            if (mOutputFluid) {
                for (boolean temp = true; temp && mFluid != null; ) {
                    temp = false;
                    FluidStack tDrained = aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(aBaseMetaTileEntity.getFrontFacing()), Math.max(1, mFluid.amount), false);
                    if (tDrained != null) {
                        int tFilledAmount = tTileEntity.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), tDrained, false);
                        if (tFilledAmount > 0) {
                            temp = true;
                            tTileEntity.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(aBaseMetaTileEntity.getFrontFacing()), tFilledAmount, true), true);
                        }
                    }
                }
            }
        }
    }

}
