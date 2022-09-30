package gregtech.api.gui.widgets;

import gregtech.api.gui.GT_Container_StorageTank;
import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_StorageTank extends GT_GUIContainerMetaTile_Machine {

    private final String mName;

    public GT_GUIContainer_StorageTank(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity, String aName) {
        super(new GT_Container_StorageTank(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "StorageTank.png");
        mName = aName;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
//        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
        fontRendererObj.drawString(mName, 8, 6, 4210752);
        if (mContainer != null) {
            fontRendererObj.drawString("Liquid Amount", 10, 20, 16448255);
            fontRendererObj.drawString(GT_Utility.parseNumberToString(((GT_Container_StorageTank) mContainer).mContent), 10, 30, 16448255);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        if (mContainer != null) {
            if (mContainer instanceof GT_Container_StorageTank) {
                if (((GT_Container_StorageTank) mContainer).mOutputFluid) {
                    drawTexturedModalRect(x + 7, y + 63, 176, 0, 18, 18);
                }
                if (((GT_Container_StorageTank) mContainer).mLockFluid) {
                    drawTexturedModalRect(x + 25, y + 63, 176, 18, 18, 18);
                }
                if (((GT_Container_StorageTank) mContainer).mVoidPartial) {
                    drawTexturedModalRect(x + 43, y + 63, 176, 36, 18, 18);
                }
            }
        }
    }
}
