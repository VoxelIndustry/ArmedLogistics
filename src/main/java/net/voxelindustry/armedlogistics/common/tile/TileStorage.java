package net.voxelindustry.armedlogistics.common.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemStorage;
import net.voxelindustry.armedlogistics.common.setup.ALContainers;
import net.voxelindustry.armedlogistics.common.setup.ALTiles;
import net.voxelindustry.steamlayer.container.ContainerBuilder;

public class TileStorage extends TileProvider
{
    public TileStorage()
    {
        super(ALTiles.STORAGE);
    }

    @Override
    protected ProviderType getProviderType()
    {
        return ProviderType.STORAGE;
    }

    @Override
    protected BaseItemStorage createItemProvider()
    {
        return new BaseItemStorage(this, getProviderType(), getWrappedInventories(),
                () -> getCable().getGridObject().getStackNetwork(), getBuffer());
    }

    public BaseItemStorage getStorage()
    {
        return (BaseItemStorage) getProvider();
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ContainerBuilder(ALContainers.STORAGE, player)
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
}
