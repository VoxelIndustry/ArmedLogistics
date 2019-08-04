package net.vi.woodengears.common.entity;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
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
import net.vi.woodengears.common.block.BlockRequester;
import net.vi.woodengears.common.grid.Path;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;
import net.vi.woodengears.common.serializer.LogisticShipmentSerializer;
import net.vi.woodengears.common.serializer.Vec3dListSerializer;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.gizmos.Gizmos;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static net.vi.woodengears.common.entity.PathToStepConverter.*;

public class EntityLogisticArm extends Entity implements ILockableContainer, IEntityAdditionalSpawnData
{
    private static final DataParameter<Float>     DAMAGE           =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> ITEM             =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer>   PATH_INDEX       =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.VARINT);
    private              NonNullList<ItemStack>   logisticArmItems = NonNullList.withSize(1, ItemStack.EMPTY);

    private boolean startPickup;
    private boolean startDrop;
    private boolean hasPickedItem;
    private boolean hasDroppedItem;

    @Getter
    private int pickupCount = 0;

    private LogisticShipment<ItemStack> shipment;

    private List<Vec3d> steps;
    private int         index       = 1;
    private int         pickupIndex = 0;
    private int         dropIndex   = 0;

    public EntityLogisticArm(World worldIn)
    {
        super(worldIn);
        setSize(0.3525F, 1.0F);
    }

    public EntityLogisticArm(World world, LogisticShipment<ItemStack> shipment, TileArmReservoir reservoir, Path reservoirToProvider, Path providerToRequester, Path requesterToReservoir)
    {
        this(world);

        Vec3d pos = new Vec3d(reservoir.getPos()).add(0.5, 0, 0.5);

        setPositionAndRotation(pos.x, pos.y - 0.0625D, pos.z, 0.0F, 90.0F);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = pos.x;
        prevPosY = pos.y;
        prevPosZ = pos.z;

        this.shipment = shipment;
        steps = createStepsFromPath(reservoirToProvider);

        pickupIndex = steps.size() + 1;
        steps.addAll(createStepsFromPath(providerToRequester));
        dropIndex = steps.size() + 1;
        steps.addAll(createStepsFromPath(requesterToReservoir));
        steps.remove(steps.size() - 1);
        steps.remove(steps.size() - 1);
        steps.add(pos);

        steps.add(0, pos);
        steps.add(1, getStartingOffset(reservoir.getPos(), reservoir.getFacing(), reservoirToProvider));
        for (Vec3d step : steps)
            Gizmos.edgedBox(step.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x00880044, 0x00FF00FF, 1)
                    .handle(() -> isDead);

        noClip = true;
    }

    @Override
    protected void entityInit()
    {
        dataManager.register(DAMAGE, 0.0F);
        dataManager.register(ITEM, ItemStack.EMPTY);
        dataManager.register(PATH_INDEX, 1);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (world.isRemote)
        {
            if (ITEM.equals(key))
                setInventorySlotContents(0, getDataManager().get(ITEM));
            else if (PATH_INDEX.equals(key))
                index = getDataManager().get(PATH_INDEX);
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    private boolean shouldMove()
    {
        if (startPickup || startDrop)
            return false;
        if (!hasPickedItem)
            return index < pickupIndex;
        if (!hasDroppedItem)
            return index < dropIndex;
        return index < steps.size();
    }

    private void completeShipment()
    {
        setDead();
    }

    @Override
    public void onUpdate()
    {
        if (steps != null)
        {
            if (shouldMove())
            {
                Vec3d previous = steps.get(index - 1);
                Vec3d pos = steps.get(index);

                Vec3d dir = pos.subtract(previous).normalize();

                move(MoverType.SELF, dir.x * getSpeed(), 0, dir.z * getSpeed());

                if (Math.abs(pos.x - posX) < getSpeed() && Math.abs(pos.z - posZ) < getSpeed())
                {
                    setPosition(pos.x, posY, pos.z);
                    incrementPathIndex();
                }
            }
            else if (!hasPickedItem && !startPickup)
                startPickup = true;
            else if (hasPickedItem && !startDrop)
                startDrop = true;
            else if (index == steps.size())
                completeShipment();
        }


        if (startPickup || startDrop)
        {
            if (pickupCount < 80)
                pickupCount++;
            else
            {
                pickupCount = 0;
                startPickup = false;
                startDrop = false;
            }
        }

        if (pickupCount == 40)
        {
            if (isOverProvider() && !hasPickedItem)
            {
                if (!world.isRemote)
                {
                    TileEntity provider = world.getTileEntity(new BlockPos(this).down());

                    if (!(provider instanceof TileProvider))
                        return;

                    setItemStack(((TileProvider) provider).getProvider().fromBuffer(shipment.getContent()));
                }
                hasPickedItem = true;
            }
            else if (isOverRequester() && hasPickedItem && !hasDroppedItem)
            {
                if (!world.isRemote)
                {
                    TileEntity requester = world.getTileEntity(new BlockPos(this).down());

                    if (!(requester instanceof TileRequester))
                        return;

                    ((TileRequester) requester).getRequester().insert(getStackInSlot(0));
                    setItemStack(ItemStack.EMPTY);
                }
                hasDroppedItem = true;
            }
        }
    }

    private List<Vec3d> createStepsFromPath(Path comingPath)
    {
        if (comingPath.getPoints().isEmpty())
            return emptyList();

        int index = 1;

        double frontOffset = 0;
        double sideOffset = 0;
        EnumFacing facing = null;

        List<Vec3d> steps = new ArrayList<>();
        while (index < comingPath.getPoints().size() - 1)
        {
            if (isNextRailCorner(index, comingPath))
            {
                BlockPos current = comingPath.getPoints().get(index);

                BlockPos previous = comingPath.getPoints().get(index - 1).subtract(current);
                BlockPos next = comingPath.getPoints().get(index + 1).subtract(current);

                Vec3d pos = new Vec3d(current);

                frontOffset = getOffsetFromCorner(previous, next);
                sideOffset = getSideOffsetFromCorner(previous, next);

                facing = EnumFacing.getFacingFromVector((float) (pos.x - previous.getX()), 0, (float) (pos.z - previous.getZ()));

                Gizmos.edgedBox(new Vec3d(previous).add(pos).add(0.5, 1, 0.5), new Vec3d(0.2D, 1, 0.2D), 0x88000044, 0xFF0000FF, 1)
                        .handle(() -> isDead);
                Gizmos.edgedBox(new Vec3d(next).add(pos).add(0.5, 1, 0.5), new Vec3d(0.2D, 1, 0.2D), 0x88000044, 0xFF0000FF, 1)
                        .handle(() -> isDead);

                if (facing.getAxis() == EnumFacing.Axis.X)
                    steps.add(pos.add(frontOffset, 0, 0.5 + sideOffset));
                else // Z-AXIS
                    steps.add(pos.add(0.5 + sideOffset, 0, frontOffset));
            }
            index++;
        }

        if (facing.getAxis() == EnumFacing.Axis.X)
        {
            steps.add(new Vec3d(comingPath.getTo()).add(frontOffset, 0, 0.5));
            steps.add(new Vec3d(comingPath.getTo()).add(0.5, 0, 0.5));
            steps.add(new Vec3d(comingPath.getTo()).add(1 - frontOffset, 0, 0.5));
        }
        else
        {
            steps.add(new Vec3d(comingPath.getTo()).add(0.5, 0, frontOffset));
            steps.add(new Vec3d(comingPath.getTo()).add(0.5, 0, 0.5));
            steps.add(new Vec3d(comingPath.getTo()).add(0.5, 0, 1 - frontOffset));
        }

        return steps;
    }

    private Vec3d getStartingOffset(BlockPos origin, EnumFacing reservoirFacing, Path path)
    {
        BlockPos current = path.getPoints().get(0);

        BlockPos previous = origin.subtract(current);
        BlockPos next = path.getPoints().get(1).subtract(current);

        double frontOffset = getOffsetFromCorner(previous, next);
        Vec3d pos = new Vec3d(origin.offset(reservoirFacing));

        EnumFacing facing = EnumFacing.getFacingFromVector((float) (current.getX() - origin.getX()), 0, (float) (current.getZ() - origin.getZ()));

        if (facing.getAxis() == EnumFacing.Axis.X)
            pos = pos.add(frontOffset, 0, 0.5);
        else // Z-AXIS
            pos = pos.add(0.5, 0, frontOffset);

        Gizmos.edgedBox(pos.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x88006644, 0xFF00AAFF, 1)
                .handle(() -> isDead);
        return pos;
    }

    private double getSpeed()
    {
        return 0.075D;
    }

    public boolean isOverProvider()
    {
        Block block = world.getBlockState(new BlockPos(this).down()).getBlock();
        return block instanceof BlockProvider;
    }

    public boolean isOverRequester()
    {
        Block block = world.getBlockState(new BlockPos(this).down()).getBlock();
        return block instanceof BlockRequester;
    }

    public boolean isBottomBlockCable()
    {
        return true;
        //Block block = this.world.getBlockState(new BlockPos(this).up()).getBlock();
        //return block instanceof BlockCable;
    }

    private void incrementPathIndex()
    {
        index++;

        if (!world.isRemote)
            dataManager.set(PATH_INDEX, index);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        ItemStackHelper.saveAllItems(tag, logisticArmItems);

        pickupCount = tag.getInteger("pickupCount");

        steps = Vec3dListSerializer.vec3dListFromNBT(tag.getCompoundTag("steps"));
        shipment = LogisticShipmentSerializer.itemShipmentFromNBT(tag.getCompoundTag("shipment"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        logisticArmItems = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, logisticArmItems);

        tag.setInteger("pickupCount", pickupCount);

        tag.setTag("steps", Vec3dListSerializer.vec3dListToNBT(steps));
        tag.setTag("shipment", LogisticShipmentSerializer.itemShipmentToNBT(shipment));
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
        for (ItemStack itemStack : logisticArmItems)
        {
            if (!itemStack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return logisticArmItems.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(logisticArmItems, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack itemStack = logisticArmItems.get(index);

        if (itemStack.isEmpty())
            return ItemStack.EMPTY;
        else
        {
            logisticArmItems.set(index, ItemStack.EMPTY);
            return itemStack;
        }
    }

    private void setItemStack(ItemStack stack)
    {
        dataManager.set(ITEM, stack);
        setInventorySlotContents(0, stack);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        logisticArmItems.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
            stack.setCount(getInventoryStackLimit());
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
        if (isDead)
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
        logisticArmItems.clear();
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
        Vec3dListSerializer.vec3dListToByteBuf(steps, buffer);
        LogisticShipmentSerializer.itemShipmentToByteBuf(shipment, buffer);

        buffer.writeInt(pickupIndex);
        buffer.writeInt(dropIndex);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        steps = Vec3dListSerializer.vec3dListFromByteBuf(buffer);
        shipment = LogisticShipmentSerializer.itemShipmentFromByteBuf(buffer);

        pickupIndex = buffer.readInt();
        dropIndex = buffer.readInt();
    }
}