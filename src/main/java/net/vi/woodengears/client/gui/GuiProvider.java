package net.vi.woodengears.client.gui;

import fr.ourten.teabeans.value.Observable;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.FilterView;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import net.voxelindustry.steamlayer.utils.ItemUtils;

public class GuiProvider extends GuiLogisticNode<TileProvider>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;
    private final FilterView    filterView;

    public GuiProvider(EntityPlayer player, TileProvider provider)
    {
        super(player, provider);

        this.setWidth(176);
        this.setHeight(216);

        mainPanel.setBackgroundTexture(BACKGROUND);

        inventoryView = new InventoryView(this, provider);
        inventoryView.getShowFiltered().setValue(provider.isShowFiltereds());
        inventoryView.getShowFiltered().addListener(this::onFilteredShownChange);
        this.getContainer().addSyncCallback("filteredShown", this::onFilteredShownSync);

        mainPanel.addChild(inventoryView, 6, 57);

        this.updateStatusStyle();
        this.getListeners().attach(provider.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.filterView = new FilterView(provider.getWhitelistProperty()::getValue, this::onWhitelistChange,
                provider.getFilters(), this::onFilterChange);
        this.getContainer().addSyncCallback("filters", this::onFilterSync);

        mainPanel.addChild(filterView, 6, 17);

        this.getListeners().attach(provider.getWhitelistProperty(),
                (obs, oldValue, newValue) ->
                {
                    filterView.refreshWhitelist(newValue);

                    if (!inventoryView.getShowFiltered().getValue())
                        inventoryView.refreshStacks(this.getTile().getCachedInventoryProperty().getValue());
                });

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/provider.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/filterview.css");
    }

    private void onFilteredShownSync(SyncedValue value)
    {
        inventoryView.getShowFiltered().setValue(this.getTile().isShowFiltereds());
    }

    private void onFilteredShownChange(Observable obs)
    {
        if (getTile().isShowFiltereds() != inventoryView.getShowFiltered().getValue())
            new ServerActionBuilder("FILTERED_SHOW_CHANGE")
                    .withBoolean("state", inventoryView.getShowFiltered().getValue()).toTile(this.getTile()).send();
    }

    private void onFilterSync(SyncedValue value)
    {
        for (int i = 0; i < this.getTile().getFilters().length; i++)
            filterView.setFilterStack(i, this.getTile().getFilters()[i].copy());

        if (!inventoryView.getShowFiltered().getValue())
            inventoryView.refreshStacks(this.getTile().getCachedInventoryProperty().getValue());
    }

    private void onWhitelistChange(boolean isWhitelist)
    {
        new ServerActionBuilder("WHITELIST_SWITCH").withBoolean("whitelist", isWhitelist).toTile(this.getTile()).send();
    }

    private void onFilterChange(int index, ItemStack value)
    {
        if (!ItemUtils.deepEquals(value, this.getTile().getFilters()[index]))
            new ServerActionBuilder("FILTER_CHANGE").withInt("index", index).withItemStack("stack", value).toTile(this.getTile()).send();
    }
}
