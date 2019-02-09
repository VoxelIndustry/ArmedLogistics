package net.vi.woodengears.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.vi.woodengears.WoodenGears;

public class ProbeCompat
{
    static int ELEMENT_FLUID;

    public static void load()
    {
        WoodenGears.logger.info("Compat module for The One Probe is loaded.");

        ELEMENT_FLUID = TheOneProbe.theOneProbeImp.registerElementFactory(FluidElement::new);

        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}