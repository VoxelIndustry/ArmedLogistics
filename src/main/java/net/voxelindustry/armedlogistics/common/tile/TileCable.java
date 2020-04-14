package net.voxelindustry.armedlogistics.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.render.WGOBJState;
import net.voxelindustry.armedlogistics.common.grid.IRailConnectable;
import net.voxelindustry.armedlogistics.common.grid.ITileRail;
import net.voxelindustry.armedlogistics.common.grid.RailGrid;
import net.voxelindustry.armedlogistics.common.setup.ALTiles;
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
    protected final EnumSet<Direction>                       renderConnections;
    @Getter
    protected final EnumMap<Direction, ITileCable<RailGrid>> connectionsMap;
    protected final EnumMap<Direction, IRailConnectable>     adjacentHandler;
    @Getter
    @Setter
    protected       int                                      grid;

    public TileCable()
    {
        super(ALTiles.CABLE);

        connectionsMap = new EnumMap<>(Direction.class);
        adjacentHandler = new EnumMap<>(Direction.class);
        grid = -1;

        renderConnections = EnumSet.noneOf(Direction.class);
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
        list.addText("Connected: " + connectionsMap.keySet().stream().map(Direction::getName).collect(Collectors.joining(" ")));
        list.addText("Handlers: " + adjacentHandler.keySet().stream().map(Direction::getName).collect(Collectors.joining(" ")));
    }

    public void connectHandler(Direction facing, IRailConnectable to, TileEntity tile)
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

    public void disconnectHandler(Direction facing, TileEntity tile)
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
    public void onChunkUnloaded()
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
        for (Direction facing : Direction.values())
        {
            if (facing == Direction.DOWN)
                scanHandlers(pos.down(2));
            if (facing.getAxis().isHorizontal())
                scanHandlers(pos.offset(facing));
        }
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        if (isClient())
        {
            if (readRenderConnections(tag))
                updateState();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);

        if (isServer())
            writeRenderConnections(tag);

        return tag;
    }

    public void scanHandlers(BlockPos posNeighbor)
    {
        TileEntity tile = world.getTileEntity(posNeighbor);

        BlockPos substracted = posNeighbor.subtract(pos);
        Direction facing = Direction.getFacingFromVector(substracted.getX(), substracted.getY(), substracted.getZ())
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
    public boolean canConnect(Direction facing, ITileNode<?> to)
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

        if (isConnected(Direction.EAST))
            rtn.append("x+");
        if (isConnected(Direction.WEST))
            rtn.append("x-");
        if (isConnected(Direction.DOWN))
            rtn.append("y-");
        if (isConnected(Direction.SOUTH))
            rtn.append("z+");
        if (isConnected(Direction.NORTH))
            rtn.append("z-");
        return rtn.toString();
    }

    private WGOBJState buildVisibilityState()
    {
        List<String> parts = new ArrayList<>();

        if (!isConnected(Direction.WEST))
        {
            parts.add("barZPosNeg");
            parts.add("backZPosNeg");
            parts.add("barZNegNeg");
            parts.add("backZNegNeg");
        }
        if (!isConnected(Direction.EAST))
        {
            parts.add("barZPosPos");
            parts.add("backZPosPos");
            parts.add("barZNegPos");
            parts.add("backZNegPos");
        }

        if (!isConnected(Direction.NORTH))
        {
            parts.add("barXPosNeg");
            parts.add("backXPosNeg");
            parts.add("barXNegNeg");
            parts.add("backXNegNeg");
        }
        if (!isConnected(Direction.SOUTH))
        {
            parts.add("barXPosPos");
            parts.add("backXPosPos");
            parts.add("barXNegPos");
            parts.add("backXNegPos");
        }

        if (isDeadEnd())
        {
            if (isConnected(Direction.EAST))
            {
                parts.add("backXPos");
                parts.add("barXPos");

                if (isConnected(Direction.DOWN))
                    parts.add("middleZ");
            }
            else if (isConnected(Direction.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");

                if (isConnected(Direction.DOWN))
                    parts.add("middleZ");
            }
            else if (isConnected(Direction.NORTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");

                if (isConnected(Direction.DOWN))
                    parts.add("middleX");
            }
            else if (isConnected(Direction.SOUTH))
            {
                parts.add("backZPos");
                parts.add("barZPos");

                if (isConnected(Direction.DOWN))
                    parts.add("middleX");
            }
        }

        if (isStraight())
        {
            if (isConnected(Direction.NORTH) || isConnected(Direction.SOUTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");
                parts.add("backZPos");
                parts.add("barZPos");

                if (isConnected(Direction.DOWN))
                    parts.add("middleX");
            }
            else if (isConnected(Direction.EAST) || isConnected(Direction.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");
                parts.add("backXPos");
                parts.add("barXPos");

                if (isConnected(Direction.DOWN))
                    parts.add("middleZ");
            }
        }

        if (!isConnected(Direction.DOWN))
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

        world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
    }

    public boolean isDeadEnd()
    {
        return renderConnections.size() <= 1 ||
                renderConnections.size() == 2 && isConnected(Direction.DOWN);
    }

    public boolean isStraight()
    {
        if (renderConnections.size() > 2)
        {
            if (renderConnections.size() != 3 || !isConnected(Direction.DOWN))
                return false;
        }
        return isConnected(Direction.NORTH) && isConnected(Direction.SOUTH)
                || isConnected(Direction.WEST) && isConnected(Direction.EAST);
    }

    public boolean isConnected(Direction facing)
    {
        return renderConnections.contains(facing);
    }

    public CompoundNBT writeRenderConnections(CompoundNBT tag)
    {
        for (Map.Entry<Direction, ITileCable<RailGrid>> entry : connectionsMap.entrySet())
            tag.putBoolean("connected" + entry.getKey().ordinal(), true);
        for (Map.Entry<Direction, IRailConnectable> entry : adjacentHandler.entrySet())
            tag.putBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    public boolean readRenderConnections(CompoundNBT tag)
    {
        int previousConnections = renderConnections.size();

        renderConnections.clear();
        for (Direction facing : Direction.values())
        {
            if (tag.contains("connected" + facing.ordinal()))
                renderConnections.add(facing);
        }
        return renderConnections.size() != previousConnections;
    }
}
