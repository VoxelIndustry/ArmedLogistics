package net.voxelindustry.armedlogistics.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.compat.top.FluidElement;

public class ProbeCompat
{
    static int ELEMENT_FLUID;

    public static void load()
    {
        ArmedLogistics.logger.info("Compat module for The One Probe is loaded.");

        ELEMENT_FLUID = TheOneProbe.theOneProbeImp.registerElementFactory(FluidElement::new);

        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}