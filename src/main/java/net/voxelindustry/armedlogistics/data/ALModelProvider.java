package net.voxelindustry.armedlogistics.data;

import com.google.common.collect.Iterators;
import net.minecraft.block.SixWayBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.setup.ALBlocks;

public class ALModelProvider extends BlockStateProvider
{
    public ALModelProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper)
    {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        Direction[] horizontals = Iterators.toArray(Plane.HORIZONTAL.iterator(), Direction.class);
        MultiPartBlockStateBuilder railBuilder = getMultipartBuilder(ALBlocks.CABLE);

        monoPart(railBuilder, horizontals);
        basePart(railBuilder, horizontals);
        wallPart(railBuilder, horizontals);
        lanePart(railBuilder, horizontals);
        crossPart(railBuilder, horizontals);
    }

    private void crossPart(MultiPartBlockStateBuilder builder, Direction... values)
    {
        for (Direction direction : values)
        {
            PartBuilder partBuilder = builder.part()
                    .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, "block/rail_wall"), existingFileHelper))
                    .uvLock(false)
                    .rotationY((int) direction.getOpposite().getHorizontalAngle())
                    .addModel();
            partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), true);

            partBuilder.useOr();
            for (Direction other : values)
            {
                if (other == direction || other == direction.getOpposite())
                    continue;
                partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(other), true);
            }

            partBuilder.end();
        }
    }

    private void lanePart(MultiPartBlockStateBuilder builder, Direction... values)
    {
        for (Direction direction : values)
        {
            PartBuilder partBuilder = builder.part()
                    .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, "block/rail_lane"), existingFileHelper))
                    .uvLock(false)
                    .rotationY((int) direction.getOpposite().getHorizontalAngle())
                    .addModel();
            partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), true);
            partBuilder.end();
        }
    }

    private void wallPart(MultiPartBlockStateBuilder builder, Direction... values)
    {
        for (Direction direction : values)
        {
            PartBuilder partBuilder = builder.part()
                    .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, "block/rail_wall"), existingFileHelper))
                    .uvLock(false)
                    .rotationY((int) direction.getHorizontalAngle())
                    .addModel();
            partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction.getOpposite()), false);
            partBuilder.end();
        }
    }

    private void basePart(MultiPartBlockStateBuilder builder, Direction... values)
    {
        PartBuilder partBuilder = builder.part()
                .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, "block/rail_base"), existingFileHelper))
                .addModel()
                .useOr();

        for (Direction direction : values)
        {
            partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), true);
        }
        partBuilder.end();
    }

    private void monoPart(MultiPartBlockStateBuilder builder, Direction... values)
    {
        PartBuilder partBuilder = builder.part()
                .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, "block/rail_single"), existingFileHelper))
                .addModel();

        for (Direction direction : values)
        {
            partBuilder.condition(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), false);
        }
        partBuilder.end();
    }
}
