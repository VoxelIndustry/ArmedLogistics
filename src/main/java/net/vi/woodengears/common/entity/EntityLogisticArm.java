package net.vi.woodengears.common.entity;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
import static net.vi.woodengears.common.entity.LogisticArmBlockCause.NONE;
import static net.vi.woodengears.common.entity.LogisticArmState.*;
import static net.vi.woodengears.common.entity.PathToStepConverter.isNextRailCorner;
import static net.vi.woodengears.common.entity.PathToStepConverter.isPathStraight;

public class EntityLogisticArm extends Entity implements IEntityAdditionalSpawnData
{
    public static double RAIL_OFFSET = 9 / 32D;

    private static final DataParameter<Float>     DAMAGE     =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> ITEM       =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer>   PATH_INDEX =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.VARINT);

    private LogisticArmState      armState      = MOVING_TO_PROVIDER;
    private LogisticArmBlockCause armBlockCause = NONE;

    @Getter
    private int pickupCount = 0;

    private LogisticShipment<ItemStack> shipment;

    private List<Vec3d> steps;
    private int         index       = 1;
    private int         pickupIndex = 0;
    private int         dropIndex   = 0;

    private Path reservoirToProvider;
    private Path providerToRequester;
    private Path requesterToReservoir;

    @Getter
    private ItemStack stack = ItemStack.EMPTY;

    public EntityLogisticArm(World worldIn)
    {
        super(worldIn);
        setSize(0.3525F, 1.0F);
    }

    public EntityLogisticArm(World world, LogisticShipment<ItemStack> shipment, TileArmReservoir reservoir, Path reservoirToProvider, Path providerToRequester, Path requesterToReservoir)
    {
        this(world);

        this.shipment = shipment;

        this.reservoirToProvider = reservoirToProvider;
        this.providerToRequester = providerToRequester;
        this.requesterToReservoir = requesterToReservoir;
        recreate(reservoirToProvider, providerToRequester, requesterToReservoir, reservoir);
        initPos();

        noClip = true;
    }

    private void initPos()
    {
        Vec3d pos = steps.get(0);

        setPositionAndRotation(pos.x, pos.y - 0.0625D, pos.z, 0.0F, 90.0F);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = pos.x;
        prevPosY = pos.y;
        prevPosZ = pos.z;
    }

    private void recreate(Path reservoirToProvider, Path providerToRequester, Path requesterToReservoir, TileArmReservoir reservoir)
    {
        requesterToReservoir.getPoints().add(reservoir.getPos());

        steps = createStepsFromPath(reservoirToProvider);

        pickupIndex = steps.size();
        steps.addAll(createStepsFromPath(providerToRequester));
        dropIndex = steps.size();
        steps.addAll(createStepsFromPath(requesterToReservoir));

        // Remove last three steps for reservoir storage
        steps.remove(steps.size() - 1);
        steps.remove(steps.size() - 1);
        steps.remove(steps.size() - 1);

        Vec3d pos = new Vec3d(reservoir.getPos()).add(0.5, 0, 0.5);

        steps.add(0, pos.add(FacingLane.fromFacing(reservoir.getFacing()).getVector()));
        steps.add(pos.add(FacingLane.fromFacing(reservoir.getFacing().getOpposite()).getVector()));

        for (Vec3d step : steps)
            Gizmos.edgedBox(step.add(0, 1, 0), new Vec3d(0.2D, 1, 0.2D), 0x00880044, 0x00FF00FF, 1)
                    .handle(() -> isDead);
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

                setPosition(posX + dir.x * getSpeed(), posY, posZ + dir.z * getSpeed());

                if (Math.abs(pos.x - posX) < getSpeed() && Math.abs(pos.z - posZ) < getSpeed())
                {
                    setPosition(pos.x, posY, pos.z);
                    incrementPathIndex();
                }
            }
            else if (armState == MOVING_TO_PROVIDER)
                armState = PICKING_FROM_PROVIDER;
            else if (armState == MOVING_TO_REQUESTER)
                armState = GIVING_TO_REQUESTER;
            else if (index == steps.size())
                completeShipment();
        }

        if (armState == PICKING_FROM_PROVIDER || armState == GIVING_TO_REQUESTER)
        {
            if (pickupCount < 80)
                pickupCount++;
            else
            {
                pickupCount = 0;
                armState = armState == PICKING_FROM_PROVIDER ? MOVING_TO_REQUESTER : MOVING_TO_RESERVOIR;
            }
        }

        if (pickupCount == 40)
        {
            if (isOverProvider() && armState == PICKING_FROM_PROVIDER)
            {
                if (!world.isRemote)
                {
                    TileEntity provider = world.getTileEntity(new BlockPos(this).down());

                    if (!(provider instanceof TileProvider))
                        return;

                    setItemStack(((TileProvider) provider).getProvider().fromBuffer(shipment.getContent()));
                }
            }
            else if (isOverRequester() && armState == GIVING_TO_REQUESTER)
            {
                if (!world.isRemote)
                {
                    TileEntity requester = world.getTileEntity(new BlockPos(this).down());

                    if (!(requester instanceof TileRequester))
                        return;

                    ((TileRequester) requester).getRequester().insert(stack);
                    setItemStack(ItemStack.EMPTY);
                }
            }
        }
    }

    private List<Vec3d> createStepsFromPath(Path path)
    {
        if (path.getPoints().isEmpty())
            return emptyList();

        int index = 1;
        boolean straightPath = isPathStraight(path);

        EnumFacing endFacing = EnumFacing.getFacingFromVector(
                path.getTo().getX() - path.getPoints().get(path.getPoints().size() - 2).getX(),
                0,
                path.getTo().getZ() - path.getPoints().get(path.getPoints().size() - 2).getZ()).getOpposite();

        List<Vec3d> steps = new ArrayList<>();

        while (!straightPath && index < path.getPoints().size() - 1)
        {
            if (isNextRailCorner(index, path))
            {
                BlockPos current = path.getPoints().get(index);

                BlockPos previous = path.getPoints().get(index - 1).subtract(current);
                BlockPos next = path.getPoints().get(index + 1).subtract(current);

                Vec3d pos = new Vec3d(current);

                EnumFacing facing = EnumFacing.getFacingFromVector(next.getX(), 0, next.getZ());

                FacingCorner facingCorner = FacingCorner.fromBlockPos(previous, next);

                Vec3d add;
                if (facingCorner == FacingCorner.WEST_NORTH)
                {
                    add = pos.add(new Vec3d(1 - RAIL_OFFSET, 0, 1 - RAIL_OFFSET));
                }
                else
                    add = pos.add(facingCorner.getVector());
                steps.add(add);

                Gizmos.text(add.subtract(0, 1, 0), facingCorner.name() + " | " + facing.name(), 0)
                        .handle(() -> isDead);
                Gizmos.edgedBox(new Vec3d(previous).add(pos).add(0.5, 1, 0.5), new Vec3d(0.2D, 1, 0.2D), 0x88000044, 0xFF0000FF, 1)
                        .handle(() -> isDead);
                Gizmos.edgedBox(new Vec3d(next).add(pos).add(0.5, 1, 0.5), new Vec3d(0.2D, 1, 0.2D), 0x88000044, 0xFF0000FF, 1)
                        .handle(() -> isDead);
            }
            index++;
        }

        Vec3d to = new Vec3d(path.getTo()).add(0.5, 0, 0.5);
        steps.add(to.add(FacingLane.fromFacing(endFacing.getOpposite()).getVector()));
        steps.add(to);
        steps.add(to.add(FacingLane.fromFacing(endFacing).getVector()));

        return steps;
    }

    private boolean shouldMove()
    {
        if (!armState.isMoving())
            return false;
        if (armState.ordinal() < PICKING_FROM_PROVIDER.ordinal())
            return index < pickupIndex;
        if (armState.ordinal() < GIVING_TO_REQUESTER.ordinal())
            return index < dropIndex;
        return index < steps.size();
    }

    private void completeShipment()
    {
        setDead();
    }

    private double getSpeed()
    {
        return 0.1D;
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

    private void setItemStack(ItemStack stack)
    {
        this.stack = stack;
        getDataManager().set(ITEM, stack);
    }

    /////////////////////
    // VANILLA METHODS //
    /////////////////////

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
                stack = getDataManager().get(ITEM);
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

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        stack = new ItemStack(tag.getCompoundTag("itemstack"));

        pickupCount = tag.getInteger("pickupCount");

        steps = Vec3dListSerializer.vec3dListFromNBT(tag.getCompoundTag("steps"));
        shipment = LogisticShipmentSerializer.itemShipmentFromNBT(tag.getCompoundTag("shipment"));

        index = tag.getInteger("currentIndex");
        pickupIndex = tag.getInteger("pickupIndex");
        dropIndex = tag.getInteger("dropIndex");

        pickupCount = tag.getInteger("pickupCount");

        armState = LogisticArmState.values()[tag.getInteger("armState")];
        armBlockCause = LogisticArmBlockCause.values()[tag.getInteger("blockState")];
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setTag("itemstack", stack.writeToNBT(new NBTTagCompound()));

        tag.setInteger("pickupCount", pickupCount);

        tag.setTag("steps", Vec3dListSerializer.vec3dListToNBT(steps));
        tag.setTag("shipment", LogisticShipmentSerializer.itemShipmentToNBT(shipment));

        tag.setInteger("currentIndex", index);
        tag.setInteger("pickupIndex", pickupIndex);
        tag.setInteger("dropIndex", dropIndex);

        tag.setInteger("pickupCount", pickupCount);
        tag.setInteger("armState", armState.ordinal());
        tag.setInteger("blockState", armBlockCause.ordinal());
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