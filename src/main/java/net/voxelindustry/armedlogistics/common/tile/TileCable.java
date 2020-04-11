package net.voxelindustry.armedlogistics.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.render.WGOBJState;
import net.voxelindustry.armedlogistics.common.grid.IRailConnectable;
import net.voxelindustry.armedlogistics.common.grid.ITileRail;
import net.voxelindustry.armedlogistics.common.grid.RailGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.grid.ITileCable;
import net.voxelindustry.steamlayer.grid.ITileNode;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TileCable extends TileBase implements ITileRail, ILoadable
{
    protected final EnumSet<EnumFacing>                       renderConnections;
    @Getter
    protected final EnumMap<EnumFacing, ITileCable<RailGrid>> connectionsMap;
    protected final EnumMap<EnumFacing, IRailConnectable>     adjacentHandler;
    @Getter
    @Setter
    protected       int                                       grid;

    public TileCable()
    {
        connectionsMap = new EnumMap<>(EnumFacing.class);
        adjacentHandler = new EnumMap<>(EnumFacing.class);
        grid = -1;

        renderConnections = EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public Collection<IRailConnectable> getConnectedHandlers()
    {
        return adjacentHandler.values();
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Grid: " + grid);
        list.addText("Connected: " + connectionsMap.keySet().stream().map(EnumFacing::getName).collect(Collectors.joining(" ")));
        list.addText("Handlers: " + adjacentHandler.keySet().stream().map(EnumFacing::getName).collect(Collectors.joining(" ")));
    }

    public void connectHandler(EnumFacing facing, IRailConnectable to, TileEntity tile)
    {
        adjacentHandler.put(facing, to);
        updateState();

        // TODO : Use a less hardcoded way to register providers

        if (hasGrid() &&
                adjacentHandler.get(facing) instanceof TileArmReservoir)
            getGridObject().addReservoir(this,
                    (TileArmReservoir) adjacentHandler.get(facing));
        if (hasGrid() &&
                adjacentHandler.get(facing) instanceof TileProvider)
            getGridObject().addProvider(this,
                    (TileProvider) adjacentHandler.get(facing));
        if (hasGrid() &&
                adjacentHandler.get(facing) instanceof TileStorage)
            getGridObject().addStorage(this,
                    (TileStorage) adjacentHandler.get(facing));
        if (hasGrid() &&
                adjacentHandler.get(facing) instanceof TileRequester)
            getGridObject().addRequester(this,
                    (TileRequester) adjacentHandler.get(facing));

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).connectTrigger(facing.getOpposite(), getGridObject());
    }

    public void disconnectHandler(EnumFacing facing, TileEntity tile)
    {
        if (hasGrid() && adjacentHandler.get(facing) instanceof TileArmReservoir)
            getGridObject().removeReservoir(this);
        if (hasGrid() && adjacentHandler.get(facing) instanceof TileProvider)
            getGridObject().removeProvider(this);
        if (hasGrid() && adjacentHandler.get(facing) instanceof TileRequester)
            getGridObject().removeRequester(this);

        adjacentHandler.remove(facing);
        updateState();

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).disconnectTrigger(facing.getOpposite(), getGridObject());
    }

    public void disconnectItself()
    {
        ArmedLogistics.instance.getGridManager().disconnectCable(this);

        adjacentHandler.keySet().forEach(facing ->
        {
            TileEntity handler = getBlockWorld().getTileEntity(getBlockPos().offset(facing));
            if (handler instanceof IConnectionAware)
                ((IConnectionAware) handler).disconnectTrigger(facing.getOpposite(), getGridObject());
        });
    }

    @Override
    public void onChunkUnload()
    {
        if (isServer())
            disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (isServer() && getGrid() == -1)
            TileTickHandler.loadables.add(this);
        else if (isClient())
        {
            askServerSync();
        }
    }

    @Override
    public void load()
    {
        ArmedLogistics.instance.getGridManager().connectCable(this);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == EnumFacing.DOWN)
                scanHandlers(pos.down(2));
            if (facing.getAxis().isHorizontal())
                scanHandlers(pos.offset(facing));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (isClient())
        {
            if (readRenderConnections(tag))
                updateState();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        if (isServer())
            writeRenderConnections(tag);

        return tag;
    }

    public void scanHandlers(BlockPos posNeighbor)
    {
        TileEntity tile = world.getTileEntity(posNeighbor);

        BlockPos substracted = posNeighbor.subtract(pos);
        EnumFacing facing = EnumFacing.getFacingFromVector(substracted.getX(), substracted.getY(), substracted.getZ())
                .getOpposite();

        if (adjacentHandler.containsKey(facing.getOpposite()))
        {
            if (!(tile instanceof IRailConnectable) || !((IRailConnectable) tile).canConnect(this, facing))
                disconnectHandler(facing.getOpposite(), tile);
        }
        else
        {
            if (tile instanceof IRailConnectable && ((IRailConnectable) tile).canConnect(this, facing))
                connectHandler(facing.getOpposite(), (IRailConnectable) tile, tile);
        }
    }

    @Override
    public BlockPos getBlockPos()
    {
        return getPos();
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        return to instanceof TileCable;
    }

    @Override
    public World getBlockWorld()
    {
        return getWorld();
    }

    @Override
    public RailGrid createGrid(int nextID)
    {
        return new RailGrid(nextID);
    }

    ////////////
    // RENDER //
    ////////////

    private static final HashMap<String, WGOBJState> variants = new HashMap<>();

    public WGOBJState getVisibilityState()
    {
        String key = getVariantKey();

        if (!variants.containsKey(key))
            variants.put(key, buildVisibilityState());
        return variants.get(key);
    }

    private String getVariantKey()
    {
        StringBuilder rtn = new StringBuilder(10);

        if (isConnected(EnumFacing.EAST))
            rtn.append("x+");
        if (isConnected(EnumFacing.WEST))
            rtn.append("x-");
        if (isConnected(EnumFacing.DOWN))
            rtn.append("y-");
        if (isConnected(EnumFacing.SOUTH))
            rtn.append("z+");
        if (isConnected(EnumFacing.NORTH))
            rtn.append("z-");
        return rtn.toString();
    }

    private WGOBJState buildVisibilityState()
    {
        List<String> parts = new ArrayList<>();

        if (!isConnected(EnumFacing.WEST))
        {
            parts.add("barZPosNeg");
            parts.add("backZPosNeg");
            parts.add("barZNegNeg");
            parts.add("backZNegNeg");
        }
        if (!isConnected(EnumFacing.EAST))
        {
            parts.add("barZPosPos");
            parts.add("backZPosPos");
            parts.add("barZNegPos");
            parts.add("backZNegPos");
        }

        if (!isConnected(EnumFacing.NORTH))
        {
            parts.add("barXPosNeg");
            parts.add("backXPosNeg");
            parts.add("barXNegNeg");
            parts.add("backXNegNeg");
        }
        if (!isConnected(EnumFacing.SOUTH))
        {
            parts.add("barXPosPos");
            parts.add("backXPosPos");
            parts.add("barXNegPos");
            parts.add("backXNegPos");
        }

        if (isDeadEnd())
        {
            if (isConnected(EnumFacing.EAST))
            {
                parts.add("backXPos");
                parts.add("barXPos");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
            else if (isConnected(EnumFacing.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
            else if (isConnected(EnumFacing.NORTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
            else if (isConnected(EnumFacing.SOUTH))
            {
                parts.add("backZPos");
                parts.add("barZPos");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
        }

        if (isStraight())
        {
            if (isConnected(EnumFacing.NORTH) || isConnected(EnumFacing.SOUTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");
                parts.add("backZPos");
                parts.add("barZPos");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
            else if (isConnected(EnumFacing.EAST) || isConnected(EnumFacing.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");
                parts.add("backXPos");
                parts.add("barXPos");

                if (isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
        }

        if (!isConnected(EnumFacing.DOWN))
        {
            parts.add("middleX");
            parts.add("middleZ");
            parts.add("middleBack");
        }
        return new WGOBJState(parts, false);
    }

    @Override
    public void updateState()
    {
        if (isServer())
        {
            sync();
            return;
        }

        world.markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    public boolean isDeadEnd()
    {
        return renderConnections.size() <= 1 ||
                renderConnections.size() == 2 && isConnected(EnumFacing.DOWN);
    }

    public boolean isStraight()
    {
        if (renderConnections.size() > 2)
        {
            if (renderConnections.size() != 3 || !isConnected(EnumFacing.DOWN))
                return false;
        }
        return isConnected(EnumFacing.NORTH) && isConnected(EnumFacing.SOUTH)
                || isConnected(EnumFacing.WEST) && isConnected(EnumFacing.EAST);
    }

    public boolean isConnected(EnumFacing facing)
    {
        return renderConnections.contains(facing);
    }

    public NBTTagCompound writeRenderConnections(NBTTagCompound tag)
    {
        for (Map.Entry<EnumFacing, ITileCable<RailGrid>> entry : connectionsMap.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        for (Map.Entry<EnumFacing, IRailConnectable> entry : adjacentHandler.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    public boolean readRenderConnections(NBTTagCompound tag)
    {
        int previousConnections = renderConnections.size();

        renderConnections.clear();
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (tag.hasKey("connected" + facing.ordinal()))
                renderConnections.add(facing);
        }
        return renderConnections.size() != previousConnections;
    }
}
