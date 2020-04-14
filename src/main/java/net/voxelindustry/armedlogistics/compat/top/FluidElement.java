package net.voxelindustry.armedlogistics.compat.top;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import mcjty.theoneprobe.api.IElement;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

@AllArgsConstructor
public class FluidElement implements IElement
{
    private FluidStack fluidStack;
    private int        amount;
    private int        capacity;

    public FluidElement(ByteBuf buf)
    {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        CompoundNBT nbt = packetBuffer.readCompoundTag();
        fluidStack = FluidStack.loadFluidStackFromNBT(nbt);

        amount = packetBuffer.readInt();
        capacity = packetBuffer.readInt();
    }

    @Override
    public void render(int x, int y)
    {
        if (fluidStack == null)
            return;

        // FIXME: Use proper fluid render
    /*    Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        ResourceLocation still = fluidStack.getFluid().get.getStill(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(still.toString());

        int width = this.getHeight();
        int height = (int) (this.getWidth() * ((float) amount / capacity));
        int iconHeight = sprite.getHeight();
        int offsetHeight = height;

        int iteration = 0;
        while (offsetHeight != 0)
        {
            final int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
            this.drawTexturedModalRect(x + height - offsetHeight, y, sprite, curHeight, width);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50)
                break;
        }*/
    }

    public void drawTexturedModalRect(int x, int y, TextureAtlasSprite textureSprite, int w, int h)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((x), (y + h), 0).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((x + w), (y + h), 0).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((x + w), (y), 0).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos((x), (y), 0).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    @Override
    public int getWidth()
    {
        return 120;
    }

    @Override
    public int getHeight()
    {
        return 12;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        CompoundNBT nbt = new CompoundNBT();

        if (fluidStack != null)
            fluidStack.writeToNBT(nbt);

        packetBuffer.writeCompoundTag(nbt);
        packetBuffer.writeInt(amount);
        packetBuffer.writeInt(capacity);
    }

    @Override
    public int getID()
    {
        return ProbeCompat.ELEMENT_FLUID;
    }
}
