package net.opmcorp.woodengears.common.tile;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.opmcorp.woodengears.common.container.BuiltContainer;
import net.opmcorp.woodengears.common.container.ContainerBuilder;
import net.opmcorp.woodengears.common.container.IContainerProvider;

import javax.annotation.Nullable;
import java.util.List;

public class TileProvider extends TileInventoryBase implements ITickable, IContainerProvider {

    @Getter
    private EnumFacing selectedFacing = EnumFacing.WEST;
    private int transferCooldown = -1;

    public TileProvider() {
        super("provider", 1);
        this.getStacks().set(0, new ItemStack(Blocks.LOG, 64));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("selectedFacing", (byte) this.selectedFacing.ordinal());
        tag.setInteger("TransferCooldown", this.transferCooldown);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        this.selectedFacing = EnumFacing.getFront(tag.getByte("selectedFacing"));
        this.transferCooldown = tag.getInteger("TransferCooldown");
    }

    public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
        IInventory iinventory = null;
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        net.minecraft.block.state.IBlockState state = worldIn.getBlockState(blockpos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state)) {
            TileEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof IInventory) {
                iinventory = (IInventory) tileentity;

                if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
                    iinventory = ((BlockChest) block).getContainer(worldIn, blockpos, true);
                }
            }
        }

        if (iinventory == null) {
            List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity) null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.HAS_INVENTORY);

            if (!list.isEmpty()) {
                iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private IInventory getInventoryForTransfer() {
        EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();
        return getInventoryAtPosition(this.getWorld(), this.pos.getX() + (double) enumfacing.getFrontOffsetX(), this.pos.getY() + (double) enumfacing.getFrontOffsetY(), this.pos.getZ() + (double) enumfacing.getFrontOffsetZ());
    }

    private boolean transferItemsOut() {
        IInventory iinventory = this.getInventoryForTransfer();

        if (iinventory == null) {
            return false;
        } else {
            EnumFacing enumfacing = this.selectedFacing;

            if (this.isInventoryFull(iinventory, enumfacing)) {
                return false;
            } else {
                ItemStack stack = new ItemStack(Blocks.LOG, 64);
                if (!stack.isEmpty()) {
                    ItemStack stack2 = stack.copy();
                    stack2.setCount(8);
                    stack.shrink(8);
                    ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, stack2, enumfacing);

                    if (itemstack1.isEmpty()) {
                        iinventory.markDirty();
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory) {
            ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k : aint) {
                ItemStack itemstack1 = isidedinventory.getStackInSlot(k);

                if (itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int i = inventoryIn.getSizeInventory();

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventoryIn.getStackInSlot(j);

                if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static ItemStack putStackInInventoryAllSlots(IInventory source, IInventory destination, ItemStack stack, @Nullable EnumFacing direction) {
        if (destination instanceof ISidedInventory && direction != null) {
            ISidedInventory isidedinventory = (ISidedInventory) destination;
            int[] aint = isidedinventory.getSlotsForFace(direction);

            for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
                stack = insertStack(source, destination, stack, aint[k], direction);
            }
        } else {
            int i = destination.getSizeInventory();

            for (int j = 0; j < i && !stack.isEmpty(); ++j) {
                stack = insertStack(source, destination, stack, j, direction);
            }
        }

        return stack;
    }

    private static ItemStack insertStack(IInventory source, IInventory destination, ItemStack stack, int index, EnumFacing direction) {
        ItemStack itemstack = destination.getStackInSlot(index);

        if (canInsertItemInSlot(destination, stack, index, direction)) {
            boolean flag = false;
            boolean flag1 = destination.isEmpty();

            if (itemstack.isEmpty()) {
                destination.setInventorySlotContents(index, stack);
                stack = ItemStack.EMPTY;
                flag = true;
            } else if (canCombine(itemstack, stack)) {
                int i = stack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag) {
                destination.markDirty();
            }
        }

        return stack;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        } else if (stack1.getMetadata() != stack2.getMetadata()) {
            return false;
        } else if (stack1.getCount() > stack1.getMaxStackSize()) {
            return false;
        } else {
            return ItemStack.areItemStackTagsEqual(stack1, stack2);
        }
    }

    private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
        if (!inventoryIn.isItemValidForSlot(index, stack)) {
            return false;
        } else {
            return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
        }
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;

            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateLogic();
            }
        }
    }

    protected boolean updateLogic() {
        if (this.world != null && !this.world.isRemote) {
            if (!this.isOnTransferCooldown()) {
                boolean flag = false;

                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }

                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player) {
        return new ContainerBuilder("provider", player)
                .player(player.inventory).inventory(8, 69).hotbar(8, 127)
                .addInventory().create();
    }
}
