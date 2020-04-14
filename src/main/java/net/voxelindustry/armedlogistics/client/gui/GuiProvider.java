package net.voxelindustry.armedlogistics.client.gui;

import fr.ourten.teabeans.value.Observable;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.gui.component.FilterView;
import net.voxelindustry.armedlogistics.client.gui.component.InventoryView;
import net.voxelindustry.armedlogistics.common.setup.ALBlocks;
import net.voxelindustry.armedlogistics.common.tile.TileActiveProvider;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;
import net.voxelindustry.armedlogistics.common.tile.TileStorage;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.steamlayer.common.utils.ItemUtils;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;

import java.util.ArrayList;
import java.util.List;

public class GuiProvider extends GuiLogisticNode<TileProvider>
{
    private static final Texture BACKGROUND = new Texture(ArmedLogistics.MODID + ":textures/gui/provider.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;
    private final FilterView    filterView;

    private ItemStack icon;

    private final List<GuiNode> elements = new ArrayList<>();

    public GuiProvider(BuiltContainer container)
    {
        super(container);

        inventoryView = new InventoryView(this, getTile());
        inventoryView.getShowFiltered().setValue(getTile().isShowFiltereds());
        inventoryView.getShowFiltered().addListener(this::onFilteredShownChange);
        getContainer().addSyncCallback("filteredShown", this::onFilteredShownSync);

        body.addChild(inventoryView, 6, 57);

        updateStatusStyle();
        getListeners().attach(getTile().getConnectedInventoryProperty(), obs -> updateStatusStyle());

        filterView = new FilterView(getTile().getWhitelistProperty()::getValue, this::onWhitelistChange,
                getTile().getFilters(), this::onFilterChange);
        getContainer().addSyncCallback("filters", this::onFilterSync);

        body.addChild(filterView, 6, 17);

        getListeners().attach(getTile().getWhitelistProperty(),
                (obs, oldValue, newValue) ->
                {
                    filterView.refreshWhitelist(newValue);

                    if (!inventoryView.getShowFiltered().getValue())
                        inventoryView.refreshStacks(getTile().getCachedInventoryProperty().getValue());
                });

        elements.add(inventoryView);
        elements.add(filterView);

        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/provider.css");
        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/inventoryview.css");
        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/filterview.css");
        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/tabheader.css");
        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/facingtab.css");
    }

    @Override
    protected Texture getBackgroundTexture()
    {
        return BACKGROUND;
    }

    @Override
    protected int getSurvivalInventoryOffset()
    {
        return 83;
    }

    @Override
    public List<GuiNode> getElements()
    {
        return elements;
    }

    @Override
    public ItemStack getIcon()
    {
        if (icon == null)
        {
            if (getTile() instanceof TileStorage)
                icon = new ItemStack(ALBlocks.STORAGE);
            else if (getTile() instanceof TileActiveProvider)
                icon = new ItemStack(ALBlocks.ACTIVE_PROVIDER);
            else
                icon = new ItemStack(ALBlocks.PROVIDER);
        }
        return icon;
    }

    private void onFilteredShownSync(SyncedValue value)
    {
        inventoryView.getShowFiltered().setValue(getTile().isShowFiltereds());
    }

    private void onFilteredShownChange(Observable obs)
    {
        if (getTile().isShowFiltereds() != inventoryView.getShowFiltered().getValue())
            new ServerActionBuilder("FILTERED_SHOW_CHANGE")
                    .withBoolean("state", inventoryView.getShowFiltered().getValue()).toTile(getTile()).send();
    }

    private void onFilterSync(SyncedValue value)
    {
        for (int i = 0; i < getTile().getFilters().length; i++)
            filterView.setFilterStack(i, getTile().getFilters()[i].copy());

        if (!inventoryView.getShowFiltered().getValue())
            inventoryView.refreshStacks(getTile().getCachedInventoryProperty().getValue());
    }

    private void onWhitelistChange(boolean isWhitelist)
    {
        new ServerActionBuilder("WHITELIST_SWITCH").withBoolean("whitelist", isWhitelist).toTile(getTile()).send();
    }

    private void onFilterChange(int index, ItemStack value)
    {
        if (!ItemUtils.deepEquals(value, getTile().getFilters()[index]))
            new ServerActionBuilder("FILTER_CHANGE").withInt("index", index).withItemStack("stack", value).toTile(getTile()).send();
    }
}
