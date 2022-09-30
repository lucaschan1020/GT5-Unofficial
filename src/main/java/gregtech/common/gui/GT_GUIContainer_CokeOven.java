package gregtech.common.gui;

import gregtech.api.gui.GT_Container_MultiMachine;
import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_CokeOven extends GT_GUIContainerMetaTile_Machine {
    String mName = "";

    public GT_GUIContainer_CokeOven(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity, String aName, String aTextureFile) {
        super(new GT_Container_MultiMachine(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "multimachines/" + (aTextureFile == null ? "MultiblockDisplay" : aTextureFile));
        mName = aName;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(mName, 10, 8, 16448255);

        if (mContainer != null) {
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 64) != 0)
                fontRendererObj.drawString(GT_Utility.trans("138", "Incomplete Structure."), 10, 64, 16448255);

            if (((GT_Container_MultiMachine) mContainer).mDisplayErrorCode == 0) {
                if (((GT_Container_MultiMachine) mContainer).mActive == 0) {
                    fontRendererObj.drawString(GT_Utility.trans("139", "Hit with Soft Mallet"), 10, 16, 16448255);
                    fontRendererObj.drawString(GT_Utility.trans("140", "to (re-)start the Machine"), 10, 24, 16448255);
                    fontRendererObj.drawString(GT_Utility.trans("141", "if it doesn't start."), 10, 32, 16448255);
                } else {
                    fontRendererObj.drawString(GT_Utility.trans("142", "Running perfectly."), 10, 16, 16448255);
                }
            }
            double tScale =
                ((double) this.mContainer.mProgressTime / (double) this.mContainer.mMaxProgressTime)
                    * 100;

            if ((int) tScale > 0 && (int) tScale < 100) {
                fontRendererObj
                    .drawString(GT_Utility.formatNumbers((int) tScale) + "/100%", 55, 40, 16448255);
            } else {
                fontRendererObj.drawString("", 65, 50, 16448255);
            }
        }
    }

    @Deprecated
    public String trans(String aKey, String aEnglish) {
        return GT_Utility.trans(aKey, aEnglish);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        if (this.mContainer != null) {
            if ((mContainer.mDisplayErrorCode & 64) != 0) {
                drawTexturedModalRect(x + 50, y + 49, 0, 251 - 5, 0, 5);
            } else {
                drawTexturedModalRect(x + 151, y + 60, 238, 0, 18, 18);
                drawTexturedModalRect(x + 115, y + 16, 238, 18, 18, 18);
                drawTexturedModalRect(x + 121, y + 34, 226, 136, 30, 38);
            }
            double tScale =
                (double) this.mContainer.mProgressTime / (double) this.mContainer.mMaxProgressTime;

            drawTexturedModalRect(x + 121, y + 34, 226, 180, 30, Math.min(68, (int) (tScale * 68)));
            drawTexturedModalRect(x + 94, y + 66, 199, 174, Math.min(58, (int) ((tScale) * 58)), 6);

        }
    }
}
