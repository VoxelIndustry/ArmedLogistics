package net.opmcorp.woodengears.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.opmcorp.woodengears.common.block.BlockCable;
import net.opmcorp.woodengears.common.entity.EntityLogisticArm;

public class ItemLogisticArm extends ItemBase
{
    public ItemLogisticArm()
    {
        super("logistic_arm");
        this.maxStackSize = 4;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlockState(pos).getBlock();

        if(!(block instanceof BlockCable))
            return EnumActionResult.FAIL;
        else
        {
            ItemStack itemStack = player.getHeldItem(hand);

            if(!world.isRemote)
            {
                EntityLogisticArm logisticArm = new EntityLogisticArm(world, pos);

                world.spawnEntity(logisticArm);
            }

            itemStack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
    }
}