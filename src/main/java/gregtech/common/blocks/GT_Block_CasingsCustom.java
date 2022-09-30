package gregtech.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class GT_Block_CasingsCustom extends GT_Block_Casings_Abstract {

    //WATCH OUT FOR TEXTURE ID's
    public GT_Block_CasingsCustom() {
        super(GT_Item_Casings8.class, "gt.blockcasingscustom", GT_Material_Casings.INSTANCE);
        for (int i = 0; i < 1; i = (i + 1)) {
            Textures.BlockIcons.casingTexturePages[7][i] = TextureFactory.of(this, i);
        }
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".0.name", "Coke Oven Brick");

        ItemList.Casing_CokeOvenBrick.set(new ItemStack(this, 1, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int aSide, int aMeta) {
        switch (aMeta) {
            case 0:
                return Textures.BlockIcons.MACHINE_CASING_COKE_OVEN_BRICK.getIcon();
        }
        return Textures.BlockIcons.MACHINE_CASING_COKE_OVEN_BRICK.getIcon();
    }
}
