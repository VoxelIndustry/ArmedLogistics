package net.vi.woodengears.common.test;

import net.minecraft.init.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WGTestExt implements BeforeAllCallback, ExtensionContext.Store.CloseableResource
{
    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context)
    {
        if (!started)
        {
            started = true;

            System.out.println("#######################################\nBootstrap Minecraft for test runner...");
            Bootstrap.register();
            System.out.println("Bootstrap complete!\nVanilla data is now accessible" +
                    ".\n######################################");
        }
    }

    @Override
    public void close()
    {
    }
}
