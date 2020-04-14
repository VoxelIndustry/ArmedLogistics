package net.voxelindustry.armedlogistics.client.gui;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public class GuiArmReservoir extends BrokkGuiContainer<BuiltContainer>
{
    private static final Texture BACKGROUND = new Texture(ArmedLogistics.MODID + ":textures/gui/arm_reservoir.png",
            0, 0, 1, 178 / 192f);

    @Getter
    private final TileArmReservoir armReservoir;

    public GuiArmReservoir(BuiltContainer container)
    {
        super(container);

        setxRelativePos(0.5f);
        setyRelativePos(0.5f);

        setWidth(176);
        setHeight(178);

        armReservoir = (TileArmReservoir) container.getMainTile();

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        mainPanel.setBackgroundTexture(BACKGROUND);
        setMainPanel(mainPanel);

        GuiLabel title = new GuiLabel(armReservoir.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 6, 6);

        // TODO : link labels with grid info

        GuiLabel busyLabel = new GuiLabel(I18n.format(ArmedLogistics.MODID + ".gui.armreservoir.busy", 2));
        busyLabel.setID("busy-label");
        busyLabel.setSize(110, 11);
        busyLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(busyLabel, 176 / 2 - 55, 9 + 4.5f);

        GuiLabel returningLabel = new GuiLabel(I18n.format(ArmedLogistics.MODID + ".gui.armreservoir.returning", 2));
        returningLabel.setID("returning-label");
        returningLabel.setSize(110, 11);
        returningLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(returningLabel, 176 / 2 - 55, 9 + 9 + 4 + 4.5f);

        GuiLabel blockedLabel = new GuiLabel(I18n.format(ArmedLogistics.MODID + ".gui.armreservoir.blocked", 2));
        blockedLabel.setID("blocked-label");
        blockedLabel.setSize(110, 11);
        blockedLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        mainPanel.addChild(blockedLabel, 176 / 2 - 55, 9 + 9 + 4 + 9 + 4 + 4.5f);

        addStylesheet("/assets/" + ArmedLogistics.MODID + "/css/armreservoir.css");
    }
}
