package net.vi.woodengears.common.grid;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class PathUtils
{
    public static void pathToByteBuf(Path path, ByteBuf buf)
    {
        buf.writeLong(path.getFrom().toLong());
        buf.writeLong(path.getTo().toLong());

        buf.writeBoolean(path.isImpossible());
        buf.writeInt(path.getPoints().size());

        path.getPoints().forEach(pos -> buf.writeLong(pos.toLong()));
    }

    public static Path pathFromByteBuf(ByteBuf buf)
    {
        Path path = new Path(BlockPos.fromLong(buf.readLong()), BlockPos.fromLong(buf.readLong()));

        path.setImpossible(buf.readBoolean());
        int size = buf.readInt();

        for (int i = 0; i < size; i++)
            path.getPoints().add(BlockPos.fromLong(buf.readLong()));

        return path;
    }

    public static NBTTagCompound pathToNBT(Path path)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setLong("fromPos", path.getFrom().toLong());
        tag.setLong("toPos", path.getTo().toLong());

        tag.setBoolean("impossible", path.isImpossible());
        tag.setInteger("count", path.getPoints().size());

        for (int i = 0; i < path.getPoints().size(); i++)
            tag.setLong("pos" + i, path.getPoints().get(i).toLong());

        return tag;
    }

    public static Path pathFromNBT(NBTTagCompound tag)
    {
        Path path = new Path(BlockPos.fromLong(tag.getLong("fromPos")), BlockPos.fromLong(tag.getLong("toPos")));

        path.setImpossible(tag.getBoolean("impossible"));

        int count = tag.getInteger("count");
        for (int i = 0; i < count; i++)
            path.getPoints().add(BlockPos.fromLong(tag.getLong("pos" + i)));

        return path;
    }
}
