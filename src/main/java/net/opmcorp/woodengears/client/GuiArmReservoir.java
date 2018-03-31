package net.opmcorp.woodengears.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.opmcorp.woodengears.WoodenGears;
import net.opmcorp.woodengears.common.tile.TileArmReservoir;

public class GuiArmReservoir extends GuiContainer
{
    private static ResourceLocation BACKGROUND = new ResourceLocation(WoodenGears.MODID,
            "textures/gui/arm_reservoir.png");

    private final TileArmReservoir armReservoir;

    public GuiArmReservoir(EntityPlayer player, TileArmReservoir armReservoir)
    {
        super(armReservoir.createContainer(player));

        this.armReservoir = armReservoir;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.armReservoir.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        this.mc.renderEngine.bindTexture(BACKGROUND);
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
