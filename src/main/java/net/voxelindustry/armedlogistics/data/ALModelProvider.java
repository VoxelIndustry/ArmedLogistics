package net.voxelindustry.armedlogistics.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
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
        new MultiPartBlockStateBuilder(ALBlocks.CABLE)
                .part()
                .modelFile(new ExistingModelFile(new ResourceLocation(ArmedLogistics.MODID, ""), existingFileHelper))
                .addModel()
    }
}
