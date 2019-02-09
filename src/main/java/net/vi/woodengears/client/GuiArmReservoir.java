package net.vi.woodengears.client;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public class GuiArmReservoir extends BrokkGuiContainer<BuiltContainer>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/arm_reservoir.png",
            0, 0, 1, 178 / 192f);

    @Getter
    private final TileArmReservoir armReservoir;

    public GuiArmReservoir(EntityPlayer player, TileArmReservoir armReservoir)
    {
        super(armReservoir.createContainer(player));

        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.setWidth(176);
        this.setHeight(178);

        this.armReservoir = armReservoir;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        mainPanel.setBackgroundTexture(BACKGROUND);
        this.setMainPanel(mainPanel);

        GuiLabel title = new GuiLabel(armReservoir.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 6, 6);

        // TODO : link labels with grid info

        GuiLabel busyLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.armreservoir.busy", 2));
        busyLabel.setID("busy-label");
        busyLabel.setSize(110, 11);
        busyLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(busyLabel, 176 / 2 - 55, 9 + 4.5f);

        GuiLabel returningLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.armreservoir.returning", 2));
        returningLabel.setID("returning-label");
        returningLabel.setSize(110, 11);
        returningLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(returningLabel, 176 / 2 - 55, 9 + 9 + 4 + 4.5f);

        GuiLabel blockedLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.armreservoir.blocked", 2));
        blockedLabel.setID("blocked-label");
        blockedLabel.setSize(110, 11);
        blockedLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(blockedLabel, 176 / 2 - 55, 9 + 9 + 4 + 9 + 4 + 4.5f);

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/armreservoir.css");
    }
}
