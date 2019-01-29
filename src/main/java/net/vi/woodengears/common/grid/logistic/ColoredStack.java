package net.vi.woodengears.common.grid.logistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.item.EnumDyeColor;

@Data
@AllArgsConstructor
public class ColoredStack
{
    private EnumDyeColor color;
    private int          quantity;
}
