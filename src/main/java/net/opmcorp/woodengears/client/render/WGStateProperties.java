package net.opmcorp.woodengears.client.render;

import net.minecraftforge.common.property.IUnlistedProperty;

public class WGStateProperties
{
    public static final IUnlistedProperty<WGOBJState> VISIBILITY_PROPERTY = new IUnlistedProperty<WGOBJState>()
    {
        public String getName()
        {
            return "qbar_visibility";
        }

        public boolean isValid(WGOBJState state)
        {
            return true;
        }

        public Class<WGOBJState> getType()
        {
            return WGOBJState.class;
        }

        public String valueToString(WGOBJState state)
        {
            return state.toString();
        }
    };
}
