package net.voxelindustry.armedlogistics.common.item;

public interface IItemModelProvider
{
    default void registerModels()
    {

    }

    default void registerVariants()
    {

    }

    default boolean hasSpecialModel()
    {
        return false;
    }
}
