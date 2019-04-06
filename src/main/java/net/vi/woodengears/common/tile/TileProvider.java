package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.IItemFilter;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.network.action.ActionSender;
import net.voxelindustry.steamlayer.network.action.IActionReceiver;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.Arrays;

public class TileProvider extends TileLogicisticNode implements ITickable, IActionReceiver, IItemFilter
{
    @Getter
    private BaseItemProvider provider;

    private WrappedInventory wrappedInventory;
    private InventoryBuffer  buffer;

    @Getter
    private BaseProperty<Boolean> whitelistProperty;

    @Getter
    private ItemStack[] filters;

    @Getter
    @Setter
    private boolean showFiltereds = true;

    public TileProvider()
    {
        super("provider");

        this.wrappedInventory = new WrappedInventory();
        this.buffer = new InventoryBuffer(8, 8 * 64);

        this.provider = new BaseItemProvider(this, ProviderType.PASSIVE_PROVIDER, this.wrappedInventory, this.buffer);

        this.getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));

        this.whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
        this.filters = new ItemStack[9];
        Arrays.fill(filters, ItemStack.EMPTY);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (!this.buffer.isEmpty())
        {
            list.addText("Buffer:");
            this.buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        this.buffer.writeNBT(tag);

        tag.setBoolean("whitelist", this.whitelistProperty.getValue());
        tag.setBoolean("filteredShown", this.showFiltereds);

        for (int i = 0; i < this.filters.length; i++)
            tag.setTag("filter" + i, this.filters[i].writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.buffer.readNBT(tag);
        this.whitelistProperty.setValue(tag.getBoolean("whitelist"));
        this.showFiltereds = tag.getBoolean("filteredShown");

        for (int i = 0; i < 9; i++)
            this.filters[i] = new ItemStack(tag.getCompoundTag("filter" + i));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("provider", player)
                .player(player).inventory(8, 134).hotbar(8, 192)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncBoolean(whitelistProperty::getValue, whitelistProperty::setValue)
                .syncInventory(this::getConnectedInventory, getCachedInventoryProperty()::setValue, 10)
                .syncArray(this::getFilters, ItemStack.class, null, "filters")
                .syncBoolean(this::isShowFiltereds, this::setShowFiltereds, "filteredShown")
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : this.buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("WHITELIST_SWITCH".equals(actionID))
        {
            this.whitelistProperty.setValue(payload.getBoolean("whitelist"));
            this.markDirty();
        }
        else if ("FILTER_CHANGE".equals(actionID))
        {
            this.filters[payload.getInteger("index")] = new ItemStack(payload.getCompoundTag("stack"));
            this.markDirty();
        }
        else if ("FILTERED_SHOW_CHANGE".equals(actionID))
        {
            this.setShowFiltereds(payload.getBoolean("state"));
            this.markDirty();
        }
    }

    @Override
    public boolean filter(ItemStack stack)
    {
        if (this.getWhitelistProperty().getValue() && this.doesFiltersContains(stack))
            return true;
        else
            return !this.getWhitelistProperty().getValue() && !this.doesFiltersContains(stack);
    }

    private boolean doesFiltersContains(ItemStack stack)
    {
        for (ItemStack filter : this.filters)
        {
            if (filter.isEmpty())
                continue;
            if (ItemUtils.deepEquals(filter, stack))
                return true;
        }
        return false;
    }
}
