package net.vi.woodengears.client.gui.tab;

import net.minecraft.util.EnumFacing;
import net.vi.woodengears.client.gui.GuiLogisticNode;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;

public class FacingTab extends GuiAbsolutePane implements IGuiTab
{
    private final List<GuiNode> elements = new ArrayList<>();

    private GuiAbsolutePane facingList;
    private GuiLogisticNode gui;

    private EnumMap<EnumFacing, Pair<FacingSlot, FacingLine>> facingSlots = new EnumMap<>(EnumFacing.class);

    public FacingTab(GuiLogisticNode gui)
    {
        this.gui = gui;

        elements.add(this);

        setSize(176, 80);

        facingSlots.put(UP, Pair.of(new FacingSlot("TOP", 2), new FacingLine(this, UP)));
        facingSlots.put(DOWN, Pair.of(new FacingSlot("BOTTOM", 2), new FacingLine(this, DOWN)));
        facingSlots.put(NORTH, Pair.of(new FacingSlot("NORTH", 2), new FacingLine(this, NORTH)));
        facingSlots.put(SOUTH, Pair.of(new FacingSlot("SOUTH", 2), new FacingLine(this, SOUTH)));
        facingSlots.put(EAST, Pair.of(new FacingSlot("EAST", 2), new FacingLine(this, EAST)));
        facingSlots.put(WEST, Pair.of(new FacingSlot("WEST", 2), new FacingLine(this, WEST)));

        facingSlots.values().forEach(this::linkButtons);

        addChild(facingSlots.get(UP).getLeft(), 61, 27);
        addChild(facingSlots.get(NORTH).getLeft(), 61, 80);
        addChild(facingSlots.get(SOUTH).getLeft(), 61, 133);

        addChild(facingSlots.get(WEST).getLeft(), 8, 80);
        addChild(facingSlots.get(EAST).getLeft(), 114, 80);
        addChild(facingSlots.get(DOWN).getLeft(), 114, 133);

        facingList = new GuiAbsolutePane();
        facingList.setSize(56, 48);
        facingList.setxTranslate(-60);
        facingList.setyTranslate(4);
        facingList.setID("facing-list");

        elements.add(facingList);

        for (Map.Entry<EnumFacing, Pair<FacingSlot, FacingLine>> facingSlot : facingSlots.entrySet())
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
        for (Map.Entry<EnumFacing, Pair<FacingSlot, FacingLine>> facingSlot : facingSlots.entrySet())
        {
            facingSlot.getValue().getLeft().getSelectedProperty().setValue(gui.getTile().getAdjacentFacings().contains(facingSlot.getKey()));
        }

        facingList.clearChilds();

        int index = 0;
        for (EnumFacing facing : gui.getTile().getAdjacentFacings())
        {
            facingList.addChild(facingSlots.get(facing).getRight(), 2, 2 + index * 12);
            index++;
        }

        facingList.setHeight(4 + facingList.getChildCount() * 12);
    }

    void removeFacing(EnumFacing facing)
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
    public float getTabOffsetX()
    {
        return 60;
    }
}
