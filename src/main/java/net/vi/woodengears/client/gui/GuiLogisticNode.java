package net.vi.woodengears.client.gui;

import fr.ourten.teabeans.binding.BaseExpression;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.vi.woodengears.client.gui.component.EditableName;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.client.gui.tab.FacingTab;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.input.GuiToggleButton;
import net.voxelindustry.brokkgui.element.input.GuiToggleGroup;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public abstract class GuiLogisticNode<T extends TileLogicisticNode> extends BrokkGuiContainer<BuiltContainer>
{
    public static final float TAB_HEIGHT = 32;

    @Getter
    private final T tile;

    protected GuiAbsolutePane tabHeaders;
    protected GuiAbsolutePane body;
    private   GuiAbsolutePane mainPanel;

    private final GuiAbsolutePane[] tabs = new GuiAbsolutePane[]{new FacingTab()};

    private GuiNode lastPane = null;

    protected EditableName title;

    public GuiLogisticNode(EntityPlayer player, T tile)
    {
        super(tile.createContainer(player));

        this.tile = tile;

        mainPanel = new GuiAbsolutePane();
        setMainPanel(mainPanel);

        body = new GuiAbsolutePane();
        body.setBackgroundTexture(getBackgroundTexture());
        mainPanel.addChild(body, 0, TAB_HEIGHT);

        setupTabs();

        title = new EditableName(tile.getDisplayName()::getFormattedText, tile::setCustomName);
        title.getWidthProperty().bind(BaseExpression.transform(body.getWidthProperty(), parentWidth -> parentWidth - 6 - 3));
        body.addChild(title, 6, 6);
    }

    protected abstract Texture getBackgroundTexture();

    protected abstract void switchMainTab(boolean isVisible);

    protected void setBodyDimension(float width, float height, float xOffset)
    {
        body.setSize(width - xOffset, height);
        setSize(width, height + TAB_HEIGHT);

        mainPanel.setChildPos(tabHeaders, xOffset, 0);
        body.setxTranslate(xOffset);
        setxOffset(-xOffset / 2);
    }

    @Override
    public GuiAbsolutePane getMainPanel()
    {
        return mainPanel;
    }

    private void setupTabs()
    {
        tabHeaders = new GuiAbsolutePane();
        tabHeaders.setSize(176, 32);

        GuiToggleGroup tabHeaderGroup = new GuiToggleGroup();

        GuiToggleButton mainTabButton = new GuiToggleButton();
        mainTabButton.addStyleClass("tab");
        mainTabButton.setSize(28, 32);
        mainTabButton.setToggleGroup(tabHeaderGroup);
        tabHeaderGroup.setSelectedButton(mainTabButton);
        tabHeaders.addChild(mainTabButton, 0, 4);

        mainTabButton.setOnSelectEvent(e ->
        {
            if (!e.isSelected())
                return;
            switchMainTab(true);
            lastPane.setVisible(false);
            lastPane = null;
        });

        for (int i = 0; i < tabs.length; i++)
        {
            GuiToggleButton tabButton = new GuiToggleButton();
            tabButton.addStyleClass("tab");
            tabButton.setSize(28, 32);
            tabButton.setToggleGroup(tabHeaderGroup);

            int finalIndex = i;
            tabButton.setOnSelectEvent(e ->
            {
                if (!e.isSelected())
                    return;

                if (lastPane == null)
                    switchMainTab(false);
                else
                    lastPane.setVisible(false);
                tabs[finalIndex].setVisible(true);
                lastPane = tabs[finalIndex];
            });

            tabHeaders.addChild(tabButton, (i + 1) * 28, 4);
            mainPanel.addChild(tabs[i], 0, 0);
            RelativeBindingHelper.bindToPos(tabs[i], body);
            RelativeBindingHelper.bindSizeRelative(tabs[i], body, 1, 1);
            tabs[i].setVisible(false);
        }
        tabHeaderGroup.setAllowNothing(false);

        mainPanel.addChild(tabHeaders);
    }

    protected void updateStatusStyle()
    {
        if (tile.getConnectedInventoryProperty().getValue())
        {
            TileEntity tile =
                    Minecraft.getMinecraft().world.getTileEntity(this.tile.getPos().offset(this.tile.getFacing()));
            String name = tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() :
                    I18n.format("woodengears.gui.inventory.genericname");

            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.where", name,
                    I18n.format("woodengears.gui.facing." + this.tile.getFacing())));
            getInventoryView().setInvValid(true);
        }
        else
        {
            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + tile.getFacing())));
            getInventoryView().setInvValid(false);
        }
    }

    protected abstract InventoryView getInventoryView();
}
