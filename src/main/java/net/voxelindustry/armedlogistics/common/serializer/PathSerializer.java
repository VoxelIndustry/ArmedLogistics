package net.voxelindustry.armedlogistics.common.serializer;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.common.grid.Path;

@UtilityClass
public class PathSerializer
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

    public static CompoundNBT pathToNBT(Path path)
    {
        CompoundNBT tag = new CompoundNBT();

        tag.putLong("fromPos", path.getFrom().toLong());
        tag.putLong("toPos", path.getTo().toLong());

        tag.putBoolean("impossible", path.isImpossible());
        tag.putInt("count", path.getPoints().size());

        for (int i = 0; i < path.getPoints().size(); i++)
            tag.putLong("pos" + i, path.getPoints().get(i).toLong());

        return tag;
    }

    public static Path pathFromNBT(CompoundNBT tag)
    {
        Path path = new Path(BlockPos.fromLong(tag.getLong("fromPos")), BlockPos.fromLong(tag.getLong("toPos")));

        path.setImpossible(tag.getBoolean("impossible"));

        int count = tag.getInt("count");
        for (int i = 0; i < count; i++)
            path.getPoints().add(BlockPos.fromLong(tag.getLong("pos" + i)));

        return path;
    }
}
