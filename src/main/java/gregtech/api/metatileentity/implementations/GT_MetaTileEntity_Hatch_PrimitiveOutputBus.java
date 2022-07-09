package gregtech.api.metatileentity.implementations;

import gregtech.api.gui.GT_Container_2by2;
import gregtech.api.gui.GT_GUIContainer_2by2;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_Utility.moveMultipleItemStacks;

public class GT_MetaTileEntity_Hatch_PrimitiveOutputBus extends GT_MetaTileEntity_Hatch {
    public GT_MetaTileEntity_Hatch_PrimitiveOutputBus(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 4, new String[]{
            "Item Output for Primitive Multiblocks",
            "Shift + right click with screwdriver to toggle automatic item shuffling",
            "Capacity: 4 stacks",
            "Does not work with regular GregTech multiblocks"});
    }

    public GT_MetaTileEntity_Hatch_PrimitiveOutputBus(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 4, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(ITEM_OUT_SIGN)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(ITEM_OUT_SIGN)};
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
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    /**
     * Attempt to store as many items as possible into the internal inventory of this output bus.
     * If you need atomicity you should use {@link gregtech.api.interfaces.tileentity.IHasInventory#addStackToSlot(int, ItemStack)}
     * @param aStack Assume valid.
     *               Will be mutated.
     *               Take over the ownership. Caller should not retain a reference to this stack if the call returns true.
     * @return true if stack is fully accepted. false is stack is partially accepted or nothing is accepted
     */
    public boolean storeAll(ItemStack aStack) {
        for (int i = 0, mInventoryLength = mInventory.length; i < mInventoryLength && aStack.stackSize > 0; i++) {
            ItemStack tSlot = mInventory[i];
            if (GT_Utility.isStackInvalid(tSlot)) {
                if (aStack.stackSize <= getInventoryStackLimit()) {
                    mInventory[i] = aStack;
                    return true;
                }
                mInventory[i] = aStack.splitStack(getInventoryStackLimit());
            } else {
                int tRealStackLimit = Math.min(getInventoryStackLimit(), tSlot.getMaxStackSize());
                if (tSlot.stackSize < tRealStackLimit &&
                    tSlot.isItemEqual(aStack) &&
                    ItemStack.areItemStackTagsEqual(tSlot, aStack)) {
                    if (aStack.stackSize + tSlot.stackSize <= tRealStackLimit) {
                        mInventory[i].stackSize += aStack.stackSize;
                        return true;
                    } else {
                        // more to serve
                        aStack.stackSize -= tRealStackLimit - tSlot.stackSize;
                        mInventory[i].stackSize = tRealStackLimit;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_PrimitiveOutputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_2by2(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_2by2(aPlayerInventory, aBaseMetaTileEntity, "Primitive Output Bus");
    }



    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aSide == aBaseMetaTileEntity.getFrontFacing();
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isAllowedToWork() && (aTick&0x7)==0) {
            IInventory tTileEntity =aBaseMetaTileEntity.getIInventoryAtSide(aBaseMetaTileEntity.getFrontFacing());
            if(tTileEntity!=null){
                moveMultipleItemStacks(aBaseMetaTileEntity,tTileEntity,aBaseMetaTileEntity.getFrontFacing(),aBaseMetaTileEntity.getBackFacing(),null,false,(byte)64,(byte)1,(byte)64,(byte)1,mInventory.length);
                for (int i = 0; i < mInventory.length; i++)
                    if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
//                GT_Utility.moveOneItemStack(aBaseMetaTileEntity, tTileEntity,
//                        aBaseMetaTileEntity.getFrontFacing(), aBaseMetaTileEntity.getBackFacing(),
//                        null, false, (byte) 64, (byte) 1, (byte)( 64 * aBaseMetaTileEntity.getSizeInventory()), (byte) 1);
            }
        }
    }
}
