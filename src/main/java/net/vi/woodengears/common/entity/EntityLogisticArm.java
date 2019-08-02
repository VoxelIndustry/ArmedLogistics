package net.vi.woodengears.common.entity;

import io.netty.buffer.ByteBuf;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.vi.woodengears.common.block.BlockProvider;
import net.vi.woodengears.common.grid.Path;
import net.vi.woodengears.common.grid.PathUtils;
import net.vi.woodengears.common.init.WGItems;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.gizmos.Gizmos;

import java.util.ArrayList;
import java.util.List;

public class EntityLogisticArm extends Entity implements ILockableContainer, IEntityAdditionalSpawnData
{
    private static final DataParameter<Float>   DAMAGE           =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);
    private              NonNullList<ItemStack> logisticArmItems = NonNullList.withSize(1, ItemStack.EMPTY);

    private boolean startPickup;
    @Getter
    private int     pickupCount = 80;

    private Path path;

    private List<Vec3d> steps;
    private int         index = 1;

    public EntityLogisticArm(World worldIn)
    {
        super(worldIn);
        this.setSize(0.3525F, 1.0F);
    }

    public EntityLogisticArm(World world, TileArmReservoir reservoir, Path path)
    {
        this(world);

        Vec3d pos = setOffsetFromPath(reservoir.getPos().offset(reservoir.getFacing()), path);

        this.setPositionAndRotation(pos.x, pos.y - 0.0625D, pos.z, 0.0F, 90.0F);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = pos.x;
        this.prevPosY = pos.y;
        this.prevPosZ = pos.z;

        this.path = path;
        this.steps = createStepsFromPath(path);

        this.steps.add(0, pos);
        for (Vec3d step : steps)
            Gizmos.edgedBox(step.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x00880044, 0x00FF00FF, 1)
                    .handle(() -> this.isDead);

        this.noClip = true;
    }

    private List<Vec3d> createStepsFromPath(Path path)
    {
        int index = 1;

        List<Vec3d> steps = new ArrayList<>();
        while (index < path.getPoints().size() - 1)
        {
            if (isNextRailCorner(index, path))
            {
                BlockPos current = path.getPoints().get(index);

                BlockPos previous = path.getPoints().get(index - 1).subtract(current);
                BlockPos next = path.getPoints().get(index + 1).subtract(current);

                Vec3d pos = new Vec3d(current);

                double frontOffset = getOffsetFromCorner(previous, next);
                double sideOffset = getSideOffsetFromCorner(previous, next);

                EnumFacing facing = EnumFacing.getFacingFromVector((float) (pos.x - previous.getX()), 0, (float) (pos.z - previous.getZ()));

                if (facing.getAxis() == EnumFacing.Axis.X)
                {
                    Vec3d interm = pos.add(0, 0, 0.5);

                    Gizmos.edgedBox(interm.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x88880044, 0xFFFF00FF, 1)
                            .handle(() -> this.isDead);
                    steps.add(pos.add(frontOffset, 0, 0.5 + sideOffset));
                }
                else // Z-AXIS
                {
                    Vec3d interm = pos.add(0.5, 0, 0);

                    Gizmos.edgedBox(interm.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x88880044, 0xFFFF00FF, 1)
                            .handle(() -> this.isDead);

                    steps.add(pos.add(0.5 + sideOffset, 0, frontOffset));
                }
            }
            index++;
        }

        steps.add(new Vec3d(path.getTo()));

        return steps;
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

    private Vec3d setOffsetFromPath(BlockPos origin, Path path)
    {
        BlockPos current = path.getPoints().get(index);

        BlockPos previous = origin.subtract(current);
        BlockPos next = path.getPoints().get(index + 1).subtract(current);

        double frontOffset = getOffsetFromCorner(previous, next);
        double sideOffset = getSideOffsetFromCorner(previous, next);
        Vec3d pos = new Vec3d(origin);

        EnumFacing facing = EnumFacing.getFacingFromVector((float) (current.getX() - origin.getX()), 0, (float) (current.getZ() - origin.getZ()));

        if (facing.getAxis() == EnumFacing.Axis.X)
        {
            pos = pos.add(frontOffset, 0, 0.5 + sideOffset);
        }
        else // Z-AXIS
        {
            pos = pos.add(0.5 + sideOffset, 0, frontOffset);
        }

        Gizmos.edgedBox(pos.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x88882244, 0xFFFF44FF, 1)
                .handle(() -> this.isDead);
        return pos;
    }

    private double getOffsetFromCorner(BlockPos previous, BlockPos next)
    {
        if (previous.getX() != 0)
        {
            if (next.getZ() == -1)
                return 1 - (0.3125 - 0.03125);
            else
                return 0.3125 - 0.03125;
        }

        if (next.getX() == -1)
            return 0.3125 - 0.03125;
        else
            return 1 - (0.3125 - 0.03125);
    }

    private double getSideOffsetFromCorner(BlockPos previous, BlockPos next)
    {
        if (previous.getX() == 1)
        {
            if (next.getZ() == -1)
                return -7 / 32D;
            else if (next.getZ() == 1)
                return -7 / 32D;
        }
        else if (previous.getX() == -1)
        {
            if (next.getZ() == -1)
                return 7 / 32D;
            else if (next.getZ() == 1)
                return 7 / 32D;
        }
        else if (previous.getZ() == -1)
        {
            if (next.getX() == -1)
                return -7 / 32D;
            else if (next.getX() == 1)
                return -7 / 32D;
        }
        else if (previous.getZ() == 1)
        {
            if (next.getX() == -1)
                return 7 / 32D;
            else if (next.getX() == 1)
                return 7 / 32D;
        }

        return 0;
    }

    private boolean isNextRailCorner(int index, Path path)
    {
        BlockPos previous = path.getPoints().get(index - 1);
        BlockPos next = path.getPoints().get(index + 1);

        return previous.getX() != next.getX() && previous.getZ() != next.getZ();
    }

    @Override
    public void onUpdate()
    {
        if (steps != null)
        {
            if (index < this.steps.size())
            {
                Vec3d previous = steps.get(index - 1);
                Vec3d pos = steps.get(index);

                Vec3d dir = pos.subtract(previous).normalize();

                System.out.println(dir);
                this.move(MoverType.SELF, dir.x * 0.025D, 0, dir.z * 0.025D);

                if (Math.abs(pos.x - this.posX) < 0.025D && Math.abs(pos.x - this.posX) < 0.025D)
                    index++;
            }
        }

        if (!this.world.isRemote)
        {
            if (!isBottomBlockCable())
            {
                this.setDead();
                if (this.world.getGameRules().getBoolean("doEntityDrops"))
                {
                    ItemStack logistic_arm = new ItemStack(WGItems.LOGISTIC_ARM);

                    InventoryHelper.dropInventoryItems(this.world, this, this);

                    this.entityDropItem(logistic_arm, 0.0F);
                }
            }
        }

        if (!startPickup && this.isHoverBlockProvider() && this.isEmpty())
            this.startPickup = true;

      /*  if (startPickup)
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
        }*/

        if (this.isHoverBlockProvider() && this.pickupCount == 40 && this.isEmpty())
        {
            BlockProvider provider = (BlockProvider) this.world.getBlockState(new BlockPos(this).down()).getBlock();
            TileProvider tileProvider = (TileProvider) provider.getRawWorldTile(this.world, new BlockPos(this).down());

            // TODO : Impl extraction
        }
    }

    public boolean isHoverBlockProvider()
    {
        Block block = this.world.getBlockState(new BlockPos(this).down()).getBlock();
        return block instanceof BlockProvider;
    }

    public boolean isBottomBlockCable()
    {
        return true;
        //Block block = this.world.getBlockState(new BlockPos(this).up()).getBlock();
        //return block instanceof BlockCable;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        ItemStackHelper.saveAllItems(compound, this.logisticArmItems);

        this.pickupCount = compound.getInteger("pickupCount");

        this.path = PathUtils.pathFromNBT(compound.getCompoundTag("path"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        this.logisticArmItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.logisticArmItems);

        compound.setInteger("pickupCount", this.pickupCount);

        compound.setTag("path", PathUtils.pathToNBT(this.path));
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

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        PathUtils.pathToByteBuf(this.path, buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        path = PathUtils.pathFromByteBuf(buffer);
    }
}