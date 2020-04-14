package net.voxelindustry.armedlogistics.compat;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.voxelindustry.armedlogistics.compat.top.ProbeCompat;

public class CompatManager
{
    public static void setup(final FMLCommonSetupEvent e)
    {
        // FIXME: Add check of top loading
        ProbeCompat.load();
    }
}
