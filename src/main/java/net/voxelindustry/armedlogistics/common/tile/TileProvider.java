package net.voxelindustry.armedlogistics.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemProvider;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.IItemFilter;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.armedlogistics.common.setup.ALContainers;
import net.voxelindustry.armedlogistics.common.setup.ALTiles;
import net.voxelindustry.steamlayer.common.utils.ItemUtils;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.network.action.ActionSender;
import net.voxelindustry.steamlayer.network.action.IActionReceiver;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import java.util.Arrays;

public class TileProvider extends TileLogicisticNode implements IActionReceiver, IItemFilter
{
    @Getter
    private BaseItemProvider provider;

    @Getter(AccessLevel.PROTECTED)
    private InventoryBuffer buffer;

    @Getter
    private BaseProperty<Boolean> whitelistProperty;

    @Getter
    private ItemStack[] filters;

    @Getter
    @Setter
    private boolean showFiltereds = true;

    public TileProvider(TileEntityType<? extends TileProvider> type)
    {
        super(type, "provider");

        buffer = new InventoryBuffer(8, 8 * 64);

        provider = createItemProvider();

        whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
        filters = new ItemStack[9];
        Arrays.fill(filters, ItemStack.EMPTY);
    }

    public TileProvider()
    {
        this(ALTiles.PROVIDER);
    }

    protected BaseItemProvider createItemProvider()
    {
        return new BaseItemProvider(this, getProviderType(), getWrappedInventories(),
                () -> getCable().getGridObject().getStackNetwork(), buffer);
    }

    protected ProviderType getProviderType()
    {
        return ProviderType.PASSIVE_PROVIDER;
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (!buffer.isEmpty())
        {
            list.addText("Buffer:");
            buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        buffer.writeNBT(tag);

        tag.putBoolean("whitelist", whitelistProperty.getValue());
        tag.putBoolean("filteredShown", showFiltereds);

        for (int i = 0; i < filters.length; i++)
            tag.put("filter" + i, filters[i].write(new CompoundNBT()));

        provider.toNBT(tag);

        return super.write(tag);
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        buffer.readNBT(tag);
        whitelistProperty.setValue(tag.getBoolean("whitelist"));
        showFiltereds = tag.getBoolean("filteredShown");

        for (int i = 0; i < 9; i++)
            filters[i] = ItemStack.read(tag.getCompound("filter" + i));

        provider.fromNBT(tag);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ContainerBuilder(ALContainers.PROVIDER, player)
                .player(player).inventory(8, 166).hotbar(8, 224)
                .emptyTile(this)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncBoolean(getWhitelistProperty()::getValue, getWhitelistProperty()::setValue)
                .syncInventory(this::getWrappedInventories, getCachedInventoryProperty()::setValue, 10)
                .syncArray(this::getFilters, ItemStack.class, null, "filters")
                .syncBoolean(this::isShowFiltereds, this::setShowFiltereds, "filteredShown")
                .syncEnumList(this::getAdjacentFacings, Direction.class, null, "facings")
                .create(windowId);
    }

    public void dropBuffer()
    {
        for (ItemStack stack : buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    @Override
    public void handle(ActionSender sender, String actionID, CompoundNBT payload)
    {
        if ("WHITELIST_SWITCH".equals(actionID))
        {
            whitelistProperty.setValue(payload.getBoolean("whitelist"));
            markDirty();
            provider.markDirty();
        }
        else if ("FILTER_CHANGE".equals(actionID))
        {
            filters[payload.getInt("index")] = ItemStack.read(payload.getCompound("stack"));
            markDirty();
            provider.markDirty();
        }
        else if ("FILTERED_SHOW_CHANGE".equals(actionID))
        {
            setShowFiltereds(payload.getBoolean("state"));
            markDirty();
        }
        else if ("FACING_ADD".equals(actionID))
        {
            addFacing(Direction.byIndex(payload.getInt("facing")));
            markDirty();
            provider.markDirty();
        }
        else if ("FACING_REMOVE".equals(actionID))
        {
            removeFacing(Direction.byIndex(payload.getInt("facing")));
            markDirty();
            provider.markDirty();
        }
        else if ("FACING_SET".equals(actionID))
        {
            setFacing(Direction.byIndex(payload.getInt("facing")), payload.getInt("index"));
            markDirty();
            provider.markDirty();
        }
    }

    @Override
    public boolean filter(ItemStack stack)
    {
        if (getWhitelistProperty().getValue() && doesFiltersContains(stack))
            return true;
        else
            return !getWhitelistProperty().getValue() && !doesFiltersContains(stack);
    }

    private boolean doesFiltersContains(ItemStack stack)
    {
        for (ItemStack filter : filters)
        {
            if (filter.isEmpty())
                continue;
            if (ItemUtils.deepEquals(filter, stack))
                return true;
        }
        return false;
    }
}
