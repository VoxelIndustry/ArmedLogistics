package net.vi.woodengears.client.gui;

import fr.ourten.teabeans.value.Observable;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.FilterView;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.init.WGBlocks;
import net.vi.woodengears.common.tile.TileActiveProvider;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiProvider extends GuiLogisticNode<TileProvider>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;
    private final FilterView    filterView;

    private ItemStack icon;

    private final List<GuiNode> elements = new ArrayList<>();

    public GuiProvider(EntityPlayer player, TileProvider provider)
    {
        super(player, provider);

        inventoryView = new InventoryView(this, provider);
        inventoryView.getShowFiltered().setValue(provider.isShowFiltereds());
        inventoryView.getShowFiltered().addListener(this::onFilteredShownChange);
        getContainer().addSyncCallback("filteredShown", this::onFilteredShownSync);

        body.addChild(inventoryView, 6, 57);

        updateStatusStyle();
        getListeners().attach(provider.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        filterView = new FilterView(provider.getWhitelistProperty()::getValue, this::onWhitelistChange,
                provider.getFilters(), this::onFilterChange);
        getContainer().addSyncCallback("filters", this::onFilterSync);

        body.addChild(filterView, 6, 17);

        getListeners().attach(provider.getWhitelistProperty(),
                (obs, oldValue, newValue) ->
                {
                    filterView.refreshWhitelist(newValue);

                    if (!inventoryView.getShowFiltered().getValue())
                        inventoryView.refreshStacks(getTile().getCachedInventoryProperty().getValue());
                });

        elements.add(inventoryView);
        elements.add(filterView);

        addStylesheet("/assets/" + WoodenGears.MODID + "/css/provider.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/filterview.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/tabheader.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/facingtab.css");
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
            if (getTile() instanceof TileActiveProvider)
                icon = new ItemStack(WGBlocks.ACTIVE_PROVIDER);
            else
                icon = new ItemStack(WGBlocks.PROVIDER);
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
