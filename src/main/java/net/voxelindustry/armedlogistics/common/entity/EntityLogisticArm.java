package net.voxelindustry.armedlogistics.common.entity;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.voxelindustry.armedlogistics.common.block.BlockProvider;
import net.voxelindustry.armedlogistics.common.block.BlockRequester;
import net.voxelindustry.armedlogistics.common.block.BlockStorage;
import net.voxelindustry.armedlogistics.common.grid.Path;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticShipment;
import net.voxelindustry.armedlogistics.common.serializer.LogisticShipmentSerializer;
import net.voxelindustry.armedlogistics.common.serializer.Vec3dListSerializer;
import net.voxelindustry.armedlogistics.common.setup.ALEntity;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;
import net.voxelindustry.armedlogistics.common.tile.TileRequester;
import net.voxelindustry.armedlogistics.common.tile.TileStorage;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.gizmos.Gizmos;

import java.util.List;

import static net.minecraft.item.ItemStack.EMPTY;
import static net.voxelindustry.armedlogistics.common.entity.LogisticArmBlockCause.NONE;
import static net.voxelindustry.armedlogistics.common.entity.LogisticArmState.*;

public class EntityLogisticArm extends Entity implements IEntityAdditionalSpawnData
{
    public static double RAIL_OFFSET = 9 / 32D;

    private static final DataParameter<Float>     DAMAGE     =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);
    private static final DataParameter<ItemStack> ITEM       =
            EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.ITEMSTACK);
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

    @Getter
    private ItemStack stack = EMPTY;

    public EntityLogisticArm(EntityType<Entity> type, World world)
    {
        super(type, world);
    }

    public EntityLogisticArm(World world, LogisticShipment<ItemStack> shipment, TileArmReservoir reservoir, Path reservoirToProvider, Path providerToRequester, Path requesterToReservoir)
    {
        this(ALEntity.LOGISTIC_ARM, world);

        this.shipment = shipment;

        recreate(reservoirToProvider.copy(), providerToRequester, requesterToReservoir.copy(), reservoir);
        initPos();

        noClip = true;
    }

    private void initPos()
    {
        Vec3d pos = steps.get(0);

        setPositionAndRotation(pos.x, pos.y - 0.0625D, pos.z, 0.0F, 90.0F);
        setMotion(0, 0, 0);
        prevPosX = pos.x;
        prevPosY = pos.y;
        prevPosZ = pos.z;
    }

    private void recreate(Path reservoirToProvider, Path providerToRequester, Path requesterToReservoir, TileArmReservoir reservoir)
    {
        reservoirToProvider.getPoints().add(0, reservoir.getPos());
        reservoirToProvider.setFrom(reservoir.getPos());

        requesterToReservoir.getPoints().add(reservoir.getPos());

        steps = PathToStepConverter.createStepsFromPath(reservoirToProvider);

        pickupIndex = steps.size();
        steps.addAll(PathToStepConverter.createStepsFromPath(providerToRequester));
        dropIndex = steps.size();
        steps.addAll(PathToStepConverter.createStepsFromPath(requesterToReservoir));

        // Remove last three steps for reservoir storage
        steps.remove(steps.size() - 1);
        steps.remove(steps.size() - 1);
        steps.remove(steps.size() - 1);

        Vec3d pos = new Vec3d(reservoir.getPos()).add(0.5, 0, 0.5);

        steps.add(0, pos.add(FacingLane.fromFacing(reservoir.getFacing()).getVector()));
        steps.add(pos.add(FacingLane.fromFacing(reservoir.getFacing().getOpposite()).getVector()));

        for (int i = 0; i < steps.size(); i++)
        {
            Vec3d vec = steps.get(i);
            Gizmos.outlineBox(vec.add(0, -0.75, 0), new Vec3d(0.5, 0.5, 0.5), 0xFFD700AA, 3F).handle(() -> !isAlive());
            Gizmos.text(vec.add(0, 0, 0), String.valueOf(i), Color.BLACK.toRGBAInt()).handle(() -> !isAlive());
        }
    }

    @Override
    public void tick()
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
                armState = PICKING;
            else if (armState == MOVING_TO_DESTINATION)
                armState = GIVING;
            else if (index == steps.size())
                completeShipment();
        }

        if (armState == PICKING || armState == GIVING)
        {
            if (pickupCount < 80)
                pickupCount++;
            else
            {
                pickupCount = 0;
                armState = armState == PICKING ? MOVING_TO_DESTINATION : MOVING_RESERVOIR;
            }
        }

        if (pickupCount == 40 && !world.isRemote)
        {
            if (isOverProvider() && armState == PICKING)
            {
                TileEntity provider = world.getTileEntity(new BlockPos(this).down());

                if (!(provider instanceof TileProvider))
                    return;

                setItemStack(((TileProvider) provider).getProvider().fromBuffer(shipment.getContent()));
            }
            else if (isOverRequester() && armState == GIVING)
            {
                TileEntity requester = world.getTileEntity(new BlockPos(this).down());

                if (!(requester instanceof TileRequester))
                    return;

                ((TileRequester) requester).getRequester().insert(stack);
                ((TileRequester) requester).getRequester().deliverShipment(shipment);
                setItemStack(EMPTY);
            }
            else if (isOverStorage() && armState == GIVING)
            {
                TileEntity storage = world.getTileEntity(new BlockPos(this).down());

                if (!(storage instanceof TileStorage))
                    return;

                ((TileStorage) storage).getStorage().insert(stack);
                ((TileStorage) storage).getStorage().deliverShipment(shipment);
                setItemStack(EMPTY);
            }
        }
    }

    private boolean shouldMove()
    {
        if (!armState.isMoving())
            return false;
        if (armState.ordinal() < PICKING.ordinal())
            return index < pickupIndex;
        if (armState.ordinal() < GIVING.ordinal())
            return index < dropIndex;
        return index < steps.size();
    }

    private void completeShipment()
    {
        remove();
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

    public boolean isOverStorage()
    {
        Block block = world.getBlockState(new BlockPos(this).down()).getBlock();
        return block instanceof BlockStorage;
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
    protected void registerData()
    {
        dataManager.register(DAMAGE, 0.0F);
        dataManager.register(ITEM, EMPTY);
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
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    protected void readAdditional(CompoundNBT tag)
    {
        stack = ItemStack.read(tag.getCompound("itemstack"));

        pickupCount = tag.getInt("pickupCount");

        steps = Vec3dListSerializer.vec3dListFromNBT(tag.getCompound("steps"));
        shipment = LogisticShipmentSerializer.itemShipmentFromNBT(tag.getCompound("shipment"));

        index = tag.getInt("currentIndex");
        pickupIndex = tag.getInt("pickupIndex");
        dropIndex = tag.getInt("dropIndex");

        pickupCount = tag.getInt("pickupCount");

        armState = LogisticArmState.values()[tag.getInt("armState")];
        armBlockCause = LogisticArmBlockCause.values()[tag.getInt("blockState")];
    }

    @Override
    public void writeAdditional(CompoundNBT tag)
    {
        tag.put("itemstack", stack.serializeNBT());

        tag.putInt("pickupCount", pickupCount);

        tag.put("steps", Vec3dListSerializer.vec3dListToNBT(steps));
        tag.put("shipment", LogisticShipmentSerializer.itemShipmentToNBT(shipment));

        tag.putInt("currentIndex", index);
        tag.putInt("pickupIndex", pickupIndex);
        tag.putInt("dropIndex", dropIndex);

        tag.putInt("pickupCount", pickupCount);
        tag.putInt("armState", armState.ordinal());
        tag.putInt("blockState", armBlockCause.ordinal());
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        Vec3dListSerializer.vec3dListToByteBuf(steps, buffer);
        LogisticShipmentSerializer.itemShipmentToByteBuf(shipment, buffer);

        buffer.writeInt(pickupIndex);
        buffer.writeInt(dropIndex);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        steps = Vec3dListSerializer.vec3dListFromByteBuf(buffer);
        shipment = LogisticShipmentSerializer.itemShipmentFromByteBuf(buffer);

        pickupIndex = buffer.readInt();
        dropIndex = buffer.readInt();
    }
}