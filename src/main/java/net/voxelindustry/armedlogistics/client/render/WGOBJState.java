package net.voxelindustry.armedlogistics.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import lombok.Getter;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.List;
import java.util.Optional;

@Getter
public class WGOBJState implements IModelState
{
    protected List<String> visibilityList;
    protected boolean      whitelist;
    public    IModelState  parent;

    public WGOBJState(List<String> visibleGroups, boolean visibility)
    {
        this(visibleGroups, visibility, TRSRTransformation.identity());
    }

    public WGOBJState(List<String> visibleGroups, boolean visibility, IModelState parent)
    {
        this.parent = parent;

        this.visibilityList = Lists.newArrayList(visibleGroups);
        this.whitelist = visibility;
    }

    private final Optional<TRSRTransformation> value = Optional.of(TRSRTransformation.identity());

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if (part.isPresent())
        {
            UnmodifiableIterator<String> parts = Models.getParts(part.get());
            if (parts.hasNext())
            {
                String name = parts.next();
                if (!parts.hasNext() && (whitelist != visibilityList.contains(name)))
                {
                    return value;
                }
            }
        }
        return Optional.empty();
    }
}
