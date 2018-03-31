package net.opmcorp.woodengears.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLogisticArm extends Entity
{
    public static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityLogisticArm.class, DataSerializers.FLOAT);

    public EntityLogisticArm(World worldIn)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
    }

    public EntityLogisticArm(World world, BlockPos pos)
    {
        this(world);
        this.setPositionAndRotation(pos.getX() + 0.5D, pos.getY() - 1.0D, pos.getZ() + 0.5D, 0.0F, 90.0F);
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
        // TODO Destroy entity if cable breaks
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }
}