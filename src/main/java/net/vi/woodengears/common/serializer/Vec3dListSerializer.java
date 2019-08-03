package net.vi.woodengears.common.serializer;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Vec3dListSerializer
{
    public static void vec3dListToByteBuf(List<Vec3d> values, ByteBuf buf)
    {
        buf.writeInt(values.size());

        values.forEach(value ->
        {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        });
    }

    public static List<Vec3d> vec3dListFromByteBuf(ByteBuf buf)
    {
        int size = buf.readInt();
        List<Vec3d> values = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
            values.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));

        return values;
    }

    public static NBTTagCompound vec3dListToNBT(List<Vec3d> values)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("size", values.size());

        for (int i = 0; i < values.size(); i++)
        {
            tag.setDouble("x" + i, values.get(i).x);
            tag.setDouble("y" + i, values.get(i).y);
            tag.setDouble("z" + i, values.get(i).z);
        }

        return tag;
    }

    public static List<Vec3d> vec3dListFromNBT(NBTTagCompound tag)
    {
        int size = tag.getInteger("size");
        List<Vec3d> values = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
            values.add(new Vec3d(tag.getDouble("x" + i), tag.getDouble("y" + i), tag.getDouble("z" + i)));

        return values;
    }
}
