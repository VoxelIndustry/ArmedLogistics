package net.opmcorp.woodengears.common.entity;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.opmcorp.woodengears.common.block.BlockCable;
import net.opmcorp.woodengears.common.block.BlockProvider;
import net.opmcorp.woodengears.common.init.WGItems;
import net.opmcorp.woodengears.common.tile.TileProvider;

public class EntityLogisticArm extends Entity implements ILockableContainer
{
    private static final DataParameter<Float>   DAMAGE           =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);
    private              NonNullList<ItemStack> logisticArmItems = NonNullList.withSize(1, ItemStack.EMPTY);

    private boolean startPickup;
    @Getter
    private int     pickupCount = 80;

    public EntityLogisticArm(World worldIn)
    {
        super(worldIn);
        this.setSize(0.3525F, 1.0F);
    }

    public EntityLogisticArm(World world, BlockPos pos)
    {
        this(world);
        this.setPositionAndRotation(pos.getX() + 0.25D, pos.getY() - 0.0625D, pos.getZ() + 0.5D, 0.0F, 90.0F);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = pos.getX();
        this.prevPosY = pos.getY();
        this.prevPosZ = pos.getZ();
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(DAMAGE, 0.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    public void onUpdate()
    {
        if (!this.world.isRemote)
        {
            if (!isBottomBlockCable())
            {
                this.setDead();
                if (this.world.getGameRules().getBoolean("doEntityDrops"))
                {
                    ItemStack logistic_arm = new ItemStack(WGItems.logistic_arm);

                    InventoryHelper.dropInventoryItems(this.world, this, this);

                    this.entityDropItem(logistic_arm, 0.0F);
                }
            }
        }

        if (!startPickup && this.isHoverBlockProvider() && this.isEmpty())
            this.startPickup = true;

        if (startPickup)
        {
            if (this.posX - (Math.floor(this.posX)) <= 0.5D)
                this.move(MoverType.SELF, 0.025D, 0, 0);
            else if (this.posZ - (Math.floor(this.posZ)) <= 0.5D)
                this.move(MoverType.SELF, 0, 0, 0.025D);
            else if (this.pickupCount != 0)
                this.pickupCount--;
            else
            {
                this.pickupCount = 80;
                this.startPickup = false;
            }
        }

        if (this.isHoverBlockProvider() && this.pickupCount == 40 && this.isEmpty())
        {
            BlockProvider provider = (BlockProvider) this.world.getBlockState(new BlockPos(this).down()).getBlock();
            TileProvider tileProvider = (TileProvider) provider.getRawWorldTile(this.world, new BlockPos(this).down());
            ItemStack stack = tileProvider.getStackInSlot(0);
            this.setInventorySlotContents(0, stack);
            tileProvider.removeStackFromSlot(0);
        }
    }

    public boolean isHoverBlockProvider()
    {
        Block block = this.world.getBlockState(new BlockPos(this).down()).getBlock();
        return block instanceof BlockProvider;
    }

    public boolean isBottomBlockCable()
    {
        Block block = this.world.getBlockState(new BlockPos(this).up()).getBlock();
        return block instanceof BlockCable;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        ItemStackHelper.saveAllItems(compound, this.logisticArmItems);

        this.pickupCount = compound.getInteger("pickupCount");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        this.logisticArmItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.logisticArmItems);

        compound.setInteger("pickupCount", this.pickupCount);
    }

    @Override
    public boolean isLocked()
    {
        return false;
    }

    @Override
    public void setLockCode(LockCode code)
    {
    }

    @Override
    public LockCode getLockCode()
    {
        return LockCode.EMPTY_CODE;
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemStack : this.logisticArmItems)
        {
            if (!itemStack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.logisticArmItems.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.logisticArmItems, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack itemStack = this.logisticArmItems.get(index);

        if (itemStack.isEmpty())
            return ItemStack.EMPTY;
        else
        {
            this.logisticArmItems.set(index, ItemStack.EMPTY);
            return itemStack;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.logisticArmItems.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.isDead)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq(this) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        this.logisticArmItems.clear();
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return null;
    }

    @Override
    public String getGuiID()
    {
        return null;
    }
}