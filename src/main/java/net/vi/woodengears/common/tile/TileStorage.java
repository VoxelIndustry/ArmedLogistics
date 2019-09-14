package net.vi.woodengears.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.BaseItemStorage;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;

public class TileStorage extends TileProvider
{
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
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("storage", player)
                .player(player).inventory(8, 166).hotbar(8, 224)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncBoolean(getWhitelistProperty()::getValue, getWhitelistProperty()::setValue)
                .syncInventory(this::getWrappedInventories, getCachedInventoryProperty()::setValue, 10)
                .syncArray(this::getFilters, ItemStack.class, null, "filters")
                .syncBoolean(this::isShowFiltereds, this::setShowFiltereds, "filteredShown")
                .syncEnumList(this::getAdjacentFacings, EnumFacing.class, null, "facings")
                .create();
    }
}
