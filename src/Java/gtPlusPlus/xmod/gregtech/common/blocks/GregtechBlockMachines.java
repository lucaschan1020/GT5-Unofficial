package gtPlusPlus.xmod.gregtech.common.blocks;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IDebugableBlock;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.BaseMetaTileEntityEx;
import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Item_Machines;
import gregtech.common.blocks.GT_Material_Machines;
import gregtech.common.render.GT_Renderer_Block;
import gtPlusPlus.core.creative.AddToCreativeTab;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.xmod.gregtech.common.Meta_GT_Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GregtechBlockMachines
        extends GT_Generic_Block
        implements IDebugableBlock, ITileEntityProvider {
    public static ThreadLocal<IGregTechTileEntity> mTemporaryTileEntity = new ThreadLocal<IGregTechTileEntity>();

    public GregtechBlockMachines() {
        super(GT_Item_Machines.class, "gt.plusplus.blockmachines", new GT_Material_Machines());
        GregTech_API.registerMachineBlock(this, -1);
        setHardness(1.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        setCreativeTab(AddToCreativeTab.tabMachines);
        this.isBlockContainer = true;
    }

    @Override
	public String getHarvestTool(int aMeta) {
        switch (aMeta / 4) {
            case 0:
                return "wrench";
            case 1:
                return "wrench";
            case 2:
                return "cutter";
            case 3:
                return "axe";
		default:
			break;
        }
        return "wrench";
    }

    @Override
	public int getHarvestLevel(int aMeta) {
        return aMeta % 4;
    }

    @Override
	protected boolean canSilkHarvest() {
        return false;
    }

    @Override
	public void onNeighborChange(IBlockAccess aWorld, int aX, int aY, int aZ, int aTileX, int aTileY, int aTileZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseTileEntity)) {
            ((BaseTileEntity) tTileEntity).onAdjacentBlockChange(aTileX, aTileY, aTileZ);
        }
    }

    @Override
	public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        super.onBlockAdded(aWorld, aX, aY, aZ);
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
	public String getUnlocalizedName() {
        return "gt.plusplus.blockmachines";
    }

    @Override
	public String getLocalizedName() {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    @Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return 0;
    }

    @Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return (GregTech_API.sMachineFlammable) && (aWorld.getBlockMetadata(aX, aY, aZ) == 0) ? 100 : 0;
    }

    @Override
	public int getRenderType() {
        if (GT_Renderer_Block.INSTANCE == null) {
            return super.getRenderType();
        }
        return GT_Renderer_Block.INSTANCE.mRenderID;
    }

    @Override
	public boolean isFireSource(World aWorld, int aX, int aY, int aZ, ForgeDirection side) {
        return (GregTech_API.sMachineFlammable) && (aWorld.getBlockMetadata(aX, aY, aZ) == 0);
    }

    @Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return (GregTech_API.sMachineFlammable) && (aWorld.getBlockMetadata(aX, aY, aZ) == 0);
    }

    @Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
	public boolean canConnectRedstone(IBlockAccess var1, int var2, int var3, int var4, int var5) {
        return true;
    }

    @Override
	public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
	public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
	public boolean hasTileEntity(int aMeta) {
        return true;
    }

    @Override
	public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
	public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
	public boolean canProvidePower() {
        return true;
    }

    @Override
	public boolean isOpaqueCube() {
        return false;
    }

    @Override
	public TileEntity createNewTileEntity(World aWorld, int aMeta) {
        return createTileEntity(aWorld, aMeta);
    }

    @Override
	public IIcon getIcon(IBlockAccess aIBlockAccess, int aX, int aY, int aZ, int aSide) {
        return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
    }

    @Override
	public IIcon getIcon(int aSide, int aMeta) {
        return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
    }

    @Override
	public boolean onBlockEventReceived(World aWorld, int aX, int aY, int aZ, int aData1, int aData2) {
        super.onBlockEventReceived(aWorld, aX, aY, aZ, aData1, aData2);
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return tTileEntity != null ? tTileEntity.receiveClientEvent(aData1, aData2) : false;
    }

    @Override
	public void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB, List outputAABB, Entity collider) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (((tTileEntity instanceof IGregTechTileEntity)) && (((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null)) {
            ((IGregTechTileEntity) tTileEntity).addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
            return;
        }
        super.addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
    }

    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (((tTileEntity instanceof IGregTechTileEntity)) && (((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null)) {
            return ((IGregTechTileEntity) tTileEntity).getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
        }
        return super.getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
    }

    @Override
	public void onEntityCollidedWithBlock(World aWorld, int aX, int aY, int aZ, Entity collider) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (((tTileEntity instanceof IGregTechTileEntity)) && (((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null)) {
            ((IGregTechTileEntity) tTileEntity).onEntityCollidedWithBlock(aWorld, aX, aY, aZ, collider);
            return;
        }
        super.onEntityCollidedWithBlock(aWorld, aX, aY, aZ, collider);
    }

    @Override //TODO
	@SideOnly(Side.CLIENT) //TODO
    public void registerBlockIcons(IIconRegister aIconRegister) {
        if (GregTech_API.sPostloadFinished) {
            Utils.LOG_INFO("Setting up Icon Register for Blocks");
            Meta_GT_Proxy.sBlockIcons = aIconRegister;

            Utils.LOG_INFO("Registering MetaTileEntity specific Textures");
            for (IMetaTileEntity tMetaTileEntity : Meta_GT_Proxy.METATILEENTITIES) {
                try {
                    if (tMetaTileEntity != null) {
                        tMetaTileEntity.registerIcons(aIconRegister);
                    }
                } catch (Throwable e) {
                    e.printStackTrace(GT_Log.err);
                }
            }
          
            Utils.LOG_INFO("Starting Block Icon Load Phase");
            System.out.println("Starting Block Icon Load Phase");
            for (Runnable tRunnable : Meta_GT_Proxy.GT_BlockIconload) {
                try {
                    tRunnable.run();
                } catch (Throwable e) {
                    e.printStackTrace(GT_Log.err);
                }
            }
            Utils.LOG_INFO("Finished Block Icon Load Phase");
            System.out.println("Finished Block Icon Load Phase");
        }
    }

    @Override
	public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        return super.getBlockHardness(aWorld, aX, aY, aZ);
    }

    @Override
	public float getPlayerRelativeBlockHardness(EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (((tTileEntity instanceof BaseMetaTileEntityEx)) && (((BaseMetaTileEntityEx) tTileEntity).privateAccess()) && (!((BaseMetaTileEntityEx) tTileEntity).playerOwnsThis(aPlayer, true))) {
            return -1.0F;
        }
        return super.getPlayerRelativeBlockHardness(aPlayer, aWorld, aX, aY, aZ);
    }

    @Override
	public boolean onBlockActivated(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer, int aSide, float par1, float par2, float par3) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity == null) {
            return false;
        }
        if(aPlayer.isSneaking()){
        	ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
        	if(tCurrentItem!=null){
        		if(!GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)){
        			return false;
        		}
        	}else {return false;}
        }
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            if (((IGregTechTileEntity) tTileEntity).getTimer() < 50L) {
                return false;
            }
            if ((!aWorld.isRemote) && (!((IGregTechTileEntity) tTileEntity).isUseableByPlayer(aPlayer))) {
                return true;
            }
            return ((IGregTechTileEntity) tTileEntity).onRightclick(aPlayer, (byte) aSide, par1, par2, par3);
        }
        return false;
    }

    @Override
	public void onBlockClicked(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) &&
                ((tTileEntity instanceof IGregTechTileEntity))) {
            ((IGregTechTileEntity) tTileEntity).onLeftclick(aPlayer);
        }
    }

    @Override
	public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            return ((IGregTechTileEntity) tTileEntity).getMetaTileID();
        }
        return 0;
    }

    @Override
	public void onBlockExploded(World aWorld, int aX, int aY, int aZ, Explosion aExplosion) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseMetaTileEntityEx)) {
            ((BaseMetaTileEntityEx) tTileEntity).doEnergyExplosion();
        }
        super.onBlockExploded(aWorld, aX, aY, aZ, aExplosion);
    }

    @Override
	public void breakBlock(World aWorld, int aX, int aY, int aZ, Block par5, int par6) {
        GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            IGregTechTileEntity tGregTechTileEntity = (IGregTechTileEntity) tTileEntity;
            Random tRandom = new Random();
            mTemporaryTileEntity.set(tGregTechTileEntity);
            for (int i = 0; i < tGregTechTileEntity.getSizeInventory(); i++) {
                ItemStack tItem = tGregTechTileEntity.getStackInSlot(i);
                if ((tItem != null) && (tItem.stackSize > 0) && (tGregTechTileEntity.isValidSlot(i))) {
                    EntityItem tItemEntity = new EntityItem(aWorld, aX + tRandom.nextFloat() * 0.8F + 0.1F, aY + tRandom.nextFloat() * 0.8F + 0.1F, aZ + tRandom.nextFloat() * 0.8F + 0.1F, new ItemStack(tItem.getItem(), tItem.stackSize, tItem.getItemDamage()));
                    if (tItem.hasTagCompound()) {
                        tItemEntity.getEntityItem().setTagCompound((NBTTagCompound) tItem.getTagCompound().copy());
                    }
                    tItemEntity.motionX = (tRandom.nextGaussian() * 0.0500000007450581D);
                    tItemEntity.motionY = (tRandom.nextGaussian() * 0.0500000007450581D + 0.2000000029802322D);
                    tItemEntity.motionZ = (tRandom.nextGaussian() * 0.0500000007450581D);
                    aWorld.spawnEntityInWorld(tItemEntity);
                    tItem.stackSize = 0;
                    tGregTechTileEntity.setInventorySlotContents(i, null);
                }
            }
        }
        super.breakBlock(aWorld, aX, aY, aZ, par5, par6);
        aWorld.removeTileEntity(aX, aY, aZ);
    }

    @Override
	public ArrayList<ItemStack> getDrops(World aWorld, int aX, int aY, int aZ, int aMeta, int aFortune) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            return ((IGregTechTileEntity) tTileEntity).getDrops();
        }
        return mTemporaryTileEntity.get() == null ? new ArrayList() : ((IGregTechTileEntity) mTemporaryTileEntity.get()).getDrops();
    }

    @Override
	public int getComparatorInputOverride(World aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) && ((tTileEntity instanceof IGregTechTileEntity))) {
            return ((IGregTechTileEntity) tTileEntity).getComparatorValue((byte) aSide);
        }
        return 0;
    }

    @Override
	public int isProvidingWeakPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        if ((aSide < 0) || (aSide > 5)) {
            return 0;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) && ((tTileEntity instanceof IGregTechTileEntity))) {
            return ((IGregTechTileEntity) tTileEntity).getOutputRedstoneSignal(GT_Utility.getOppositeSide(aSide));
        }
        return 0;
    }

    @Override
	public int isProvidingStrongPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        if ((aSide < 0) || (aSide > 5)) {
            return 0;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) && ((tTileEntity instanceof IGregTechTileEntity))) {
            return ((IGregTechTileEntity) tTileEntity).getStrongOutputRedstoneSignal(GT_Utility.getOppositeSide(aSide));
        }
        return 0;
    }

    @Override
	public void dropBlockAsItemWithChance(World aWorld, int aX, int aY, int aZ, int par5, float chance, int par7) {
        if (!aWorld.isRemote) {
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if ((tTileEntity != null) && (chance < 1.0F)) {
                if (((tTileEntity instanceof BaseMetaTileEntityEx)) && (GregTech_API.sMachineNonWrenchExplosions)) {
                    ((BaseMetaTileEntityEx) tTileEntity).doEnergyExplosion();
                }
            } else {
                super.dropBlockAsItemWithChance(aWorld, aX, aY, aZ, par5, chance, par7);
            }
        }
    }

    @Override
	public boolean isSideSolid(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        if (aWorld.getBlockMetadata(aX, aY, aZ) == 0) {
            return true;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity != null) {
            if ((tTileEntity instanceof BaseMetaTileEntityEx)) {
                return true;
            }
            if (((tTileEntity instanceof BaseMetaPipeEntity)) && ((((BaseMetaPipeEntity) tTileEntity).mConnections & 0xFFFFFFC0) != 0)) {
                return true;
            }
            if (((tTileEntity instanceof ICoverable)) && (((ICoverable) tTileEntity).getCoverIDAtSide((byte) aSide.ordinal()) != 0)) {
                return true;
            }
        }
        return false;
    }

    @Override
	public int getLightOpacity(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity == null) {
            return 0;
        }
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            return ((IGregTechTileEntity) tTileEntity).getLightOpacity();
        }
        return aWorld.getBlockMetadata(aX, aY, aZ) == 0 ? 255 : 0;
    }

    @Override
	public int getLightValue(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseMetaTileEntityEx)) {
            return ((BaseMetaTileEntityEx) tTileEntity).getLightValue();
        }
        return 0;
    }

    @Override //TODO
	public TileEntity createTileEntity(World aWorld, int aMeta) {
        if (aMeta < 4) {
            return Meta_GT_Proxy.constructBaseMetaTileEntity();
        }
        return new BaseMetaPipeEntity();
    }

    @Override
	public float getExplosionResistance(Entity par1Entity, World aWorld, int aX, int aY, int aZ, double explosionX, double explosionY, double explosionZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) && ((tTileEntity instanceof IGregTechTileEntity))) {
            return ((IGregTechTileEntity) tTileEntity).getBlastResistance((byte) 6);
        }
        return 10.0F;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 1; i < Meta_GT_Proxy.METATILEENTITIES.length; i++) {
            if (Meta_GT_Proxy.METATILEENTITIES[i] != null) {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @Override
	public void onBlockPlacedBy(World aWorld, int aX, int aY, int aZ, EntityLivingBase aPlayer, ItemStack aStack) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity == null) {
            return;
        }
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            IGregTechTileEntity var6 = (IGregTechTileEntity) tTileEntity;
            if (aPlayer == null) {
                var6.setFrontFacing((byte) 1);
            } else {
                int var7 = MathHelper.floor_double(aPlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
                int var8 = Math.round(aPlayer.rotationPitch);
                if ((var8 >= 65) && (var6.isValidFacing((byte) 1))) {
                    var6.setFrontFacing((byte) 1);
                } else if ((var8 <= -65) && (var6.isValidFacing((byte) 0))) {
                    var6.setFrontFacing((byte) 0);
                } else {
                    switch (var7) {
                        case 0:
                            var6.setFrontFacing((byte) 2);
                            break;
                        case 1:
                            var6.setFrontFacing((byte) 5);
                            break;
                        case 2:
                            var6.setFrontFacing((byte) 3);
                            break;
                        case 3:
                            var6.setFrontFacing((byte) 4);
                    }
                }
            }
        }
    }

    @Override
	public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aX, int aY, int aZ, int aLogLevel) {
        TileEntity tTileEntity = aPlayer.worldObj.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseMetaTileEntityEx)) {
            return ((BaseMetaTileEntityEx) tTileEntity).getDebugInfo(aPlayer, aLogLevel);
        }
        if ((tTileEntity instanceof BaseMetaPipeEntity)) {
            return ((BaseMetaPipeEntity) tTileEntity).getDebugInfo(aPlayer, aLogLevel);
        }
        return null;
    }

    @Override
	public boolean recolourBlock(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide, int aColor) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            if (((IGregTechTileEntity) tTileEntity).getColorization() == (byte) ((aColor ^ 0xFFFFFFFF) & 0xF)) {
                return false;
            }
            ((IGregTechTileEntity) tTileEntity).setColorization((byte) ((aColor ^ 0xFFFFFFFF) & 0xF));
            return true;
        }
        return false;
    }
}
