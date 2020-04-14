package net.voxelindustry.armedlogistics.client.gui.tab;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.gui.GuiLogisticNode;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.Direction.*;

public class FacingTab extends GuiAbsolutePane implements IGuiTab
{
    private final List<GuiNode> elements = new ArrayList<>();

    private GuiAbsolutePane facingList;
    private GuiLogisticNode gui;

    private ItemStack icon = ItemStack.EMPTY;

    private TabButton button;
    private GuiLabel  invLabel;

    private EnumMap<Direction, Pair<FacingSlot, FacingLine>> facingSlots = new EnumMap<>(Direction.class);

    public FacingTab(GuiLogisticNode gui)
    {
        this.gui = gui;

        elements.add(this);

        setSize(176, 80);

        invLabel = new GuiLabel("");
        invLabel.setExpandToText(true);
        invLabel.setHeight(9);
        addChild(invLabel, 6, 6);

        for (Direction facing : values())
            facingSlots.put(facing, Pair.of(new FacingSlot(facing, I18n.format(ArmedLogistics.MODID + ".gui.facinglist.cardinal." + facing.getName2()), 2), new FacingLine(this, facing)));

        facingSlots.values().forEach(this::linkButtons);

        addChild(facingSlots.get(UP).getLeft(), 61, 27);
        addChild(facingSlots.get(NORTH).getLeft(), 61, 80);
        addChild(facingSlots.get(SOUTH).getLeft(), 61, 133);

        addChild(facingSlots.get(WEST).getLeft(), 8, 80);
        addChild(facingSlots.get(EAST).getLeft(), 114, 80);
        addChild(facingSlots.get(DOWN).getLeft(), 114, 133);

        facingList = new GuiAbsolutePane();
        facingList.setSize(56, 16);
        facingList.setxTranslate(-60);
        facingList.setyTranslate(4);
        facingList.setID("facing-list");
        elements.add(facingList);

        GuiLabel facingListTitle = new GuiLabel(I18n.format("armedlogistics.gui.facinglist.title"));
        facingListTitle.setExpandToText(true);
        facingListTitle.setID("facing-list-title");
        facingList.addChild(facingListTitle, 2, 2);

        for (Map.Entry<Direction, Pair<FacingSlot, FacingLine>> facingSlot : facingSlots.entrySet())
        {
            facingSlot.getValue().getLeft().setOnSelectEvent(e ->
            {
                if (e.isSelected() && !gui.getTile().getAdjacentFacings().contains(facingSlot.getKey()))
                    new ServerActionBuilder("FACING_ADD")
                            .withInt("facing", facingSlot.getKey().getIndex())
                            .toTile(gui.getTile())
                            .send();
                else if (!e.isSelected() && gui.getTile().getAdjacentFacings().contains(facingSlot.getKey()))
                    new ServerActionBuilder("FACING_REMOVE")
                            .withInt("facing", facingSlot.getKey().getIndex())
                            .toTile(gui.getTile())
                            .send();
            });
        }

        ((BuiltContainer) gui.getContainer()).addSyncCallback("facings", this::onFacingsSync);
        gui.getListeners().attach(gui.getTile().getCachedInventoryProperty(), obs -> refreshConnectedInv());
    }

    private void refreshConnectedInv()
    {
        if (!gui.getTile().getConnectedInventoryProperty().getValue())
        {
            invLabel.setText(I18n.format(ArmedLogistics.MODID + ".gui.facingtab.noinv"));
            return;
        }

        BlockPos adjacentPos = gui.getTile().getPos().offset(gui.getTile().getFacing());
        BlockState state = Minecraft.getInstance().world.getBlockState(adjacentPos);
        icon = state.getBlock().getItem(Minecraft.getInstance().world, adjacentPos, state);

        invLabel.setText(state.getBlock().getNameTextComponent().getFormattedText());

        if (button != null)
            button.setIconStack(icon);
    }

    @Override
    public void setButton(TabButton button)
    {
        this.button = button;

        refreshConnectedInv();
    }

    private void linkButtons(Pair<FacingSlot, FacingLine> buttons)
    {
        buttons.getLeft().setOnHoverEvent(e ->
        {
            if (e.isEntering())
                buttons.getRight().addStyleClass("fakehover");
            else
                buttons.getRight().removeStyleClass("fakehover");
        });
        buttons.getRight().setOnHoverEvent(e ->
        {
            if (e.isEntering())
                buttons.getLeft().addStyleClass("fakehover");
            else
                buttons.getLeft().removeStyleClass("fakehover");
        });
    }

    private void onFacingsSync(SyncedValue value)
    {
        for (Map.Entry<Direction, Pair<FacingSlot, FacingLine>> facingSlot : facingSlots.entrySet())
        {
            facingSlot.getValue().getLeft().getSelectedProperty().setValue(gui.getTile().getAdjacentFacings().contains(facingSlot.getKey()));
        }

        facingList.removeChildrenOfType(FacingLine.class);

        int index = 0;
        for (Direction facing : gui.getTile().getAdjacentFacings())
        {
            facingList.addChild(facingSlots.get(facing).getRight(), 2, 14 + index * 12);
            index++;
        }

        facingList.setHeight(4 + facingList.getChildCount() * 12);
    }

    void removeFacing(Direction facing)
    {
        if (gui.getTile().getAdjacentFacings().contains(facing))
            new ServerActionBuilder("FACING_REMOVE")
                    .withInt("facing", facing.getIndex())
                    .toTile(gui.getTile())
                    .send();
    }

    @Override
    public List<GuiNode> getElements()
    {
        return elements;
    }

    @Override
    public ItemStack getIcon()
    {
        return icon;
    }

    @Override
    public String getName()
    {
        return I18n.format(ArmedLogistics.MODID + ".gui.facingtab.name");
    }

    @Override
    public float getTabOffsetX()
    {
        return 60;
    }
}
