package net.vi.woodengears.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.render.WGOBJState;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.vi.woodengears.common.grid.ITileRail;
import net.vi.woodengears.common.grid.RailGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.grid.ITileCable;
import net.voxelindustry.steamlayer.grid.ITileNode;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

import java.util.*;
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
        this.connectionsMap = new EnumMap<>(EnumFacing.class);
        this.adjacentHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;

        this.renderConnections = EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public Collection<IRailConnectable> getConnectedHandlers()
    {
        return this.adjacentHandler.values();
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Grid: " + this.grid);
        list.addText("Connected: " + this.connectionsMap.keySet().stream().map(EnumFacing::getName).collect(Collectors.joining(" ")));
        list.addText("Handlers: " + this.adjacentHandler.keySet().stream().map(EnumFacing::getName).collect(Collectors.joining(" ")));
    }

    public void connectHandler(EnumFacing facing, IRailConnectable to, TileEntity tile)
    {
        this.adjacentHandler.put(facing, to);
        this.updateState();

        if (this.hasGrid() &&
                this.adjacentHandler.get(facing) instanceof TileArmReservoir)
            this.getGridObject().addReservoir(this,
                    (TileArmReservoir) this.adjacentHandler.get(facing));
        if (this.hasGrid() &&
                this.adjacentHandler.get(facing) instanceof TileProvider)
            this.getGridObject().addProvider(this,
                    (TileProvider) this.adjacentHandler.get(facing));
        if (this.hasGrid() &&
                this.adjacentHandler.get(facing) instanceof TileRequester)
            this.getGridObject().addRequester(this,
                    (TileRequester) this.adjacentHandler.get(facing));

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).connectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectHandler(EnumFacing facing, TileEntity tile)
    {
        if (this.hasGrid() && this.adjacentHandler.get(facing) instanceof TileArmReservoir)
            this.getGridObject().removeReservoir(this);
        if (this.hasGrid() && this.adjacentHandler.get(facing) instanceof TileProvider)
            this.getGridObject().removeProvider(this);
        if (this.hasGrid() && this.adjacentHandler.get(facing) instanceof TileRequester)
            this.getGridObject().removeRequester(this);

        this.adjacentHandler.remove(facing);
        this.updateState();

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).disconnectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectItself()
    {
        WoodenGears.instance.getGridManager().disconnectCable(this);

        this.adjacentHandler.keySet().forEach(facing ->
        {
            TileEntity handler = this.getBlockWorld().getTileEntity(this.getBlockPos().offset(facing));
            if (handler instanceof IConnectionAware)
                ((IConnectionAware) handler).disconnectTrigger(facing.getOpposite(), this.getGridObject());
        });
    }

    @Override
    public void onChunkUnload()
    {
        if (this.isServer())
            this.disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.isServer() && this.getGrid() == -1)
            TileTickHandler.loadables.add(this);
        else if (this.isClient())
        {
            this.askServerSync();
        }
    }

    @Override
    public void load()
    {
        WoodenGears.instance.getGridManager().connectCable(this);
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == EnumFacing.DOWN)
                this.scanHandlers(this.pos.down(2));
            if (facing.getAxis().isHorizontal())
                this.scanHandlers(this.pos.offset(facing));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (this.isClient())
        {
            if (this.readRenderConnections(tag))
                this.updateState();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        if (this.isServer())
            this.writeRenderConnections(tag);

        return tag;
    }

    public void scanHandlers(BlockPos posNeighbor)
    {
        TileEntity tile = this.world.getTileEntity(posNeighbor);

        BlockPos substracted = posNeighbor.subtract(this.pos);
        EnumFacing facing = EnumFacing.getFacingFromVector(substracted.getX(), substracted.getY(), substracted.getZ())
                .getOpposite();

        if (this.adjacentHandler.containsKey(facing.getOpposite()))
        {
            if (!(tile instanceof IRailConnectable) || !((IRailConnectable) tile).canConnect(this, facing))
                this.disconnectHandler(facing.getOpposite(), tile);
        }
        else
        {
            if (tile instanceof IRailConnectable && ((IRailConnectable) tile).canConnect(this, facing))
                this.connectHandler(facing.getOpposite(), (IRailConnectable) tile, tile);
        }
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getPos();
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        return to instanceof TileCable;
    }

    @Override
    public World getBlockWorld()
    {
        return this.getWorld();
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
        String key = this.getVariantKey();

        if (!variants.containsKey(key))
            variants.put(key, buildVisibilityState());
        return variants.get(key);
    }

    private String getVariantKey()
    {
        StringBuilder rtn = new StringBuilder(10);

        if (this.isConnected(EnumFacing.EAST))
            rtn.append("x+");
        if (this.isConnected(EnumFacing.WEST))
            rtn.append("x-");
        if (this.isConnected(EnumFacing.DOWN))
            rtn.append("y-");
        if (this.isConnected(EnumFacing.SOUTH))
            rtn.append("z+");
        if (this.isConnected(EnumFacing.NORTH))
            rtn.append("z-");
        return rtn.toString();
    }

    private WGOBJState buildVisibilityState()
    {
        List<String> parts = new ArrayList<>();

        if (!this.isConnected(EnumFacing.WEST))
        {
            parts.add("barZPosNeg");
            parts.add("backZPosNeg");
            parts.add("barZNegNeg");
            parts.add("backZNegNeg");
        }
        if (!this.isConnected(EnumFacing.EAST))
        {
            parts.add("barZPosPos");
            parts.add("backZPosPos");
            parts.add("barZNegPos");
            parts.add("backZNegPos");
        }

        if (!this.isConnected(EnumFacing.NORTH))
        {
            parts.add("barXPosNeg");
            parts.add("backXPosNeg");
            parts.add("barXNegNeg");
            parts.add("backXNegNeg");
        }
        if (!this.isConnected(EnumFacing.SOUTH))
        {
            parts.add("barXPosPos");
            parts.add("backXPosPos");
            parts.add("barXNegPos");
            parts.add("backXNegPos");
        }

        if (this.isDeadEnd())
        {
            if (this.isConnected(EnumFacing.EAST))
            {
                parts.add("backXPos");
                parts.add("barXPos");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
            else if (this.isConnected(EnumFacing.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
            else if (this.isConnected(EnumFacing.NORTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
            else if (this.isConnected(EnumFacing.SOUTH))
            {
                parts.add("backZPos");
                parts.add("barZPos");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
        }

        if (this.isStraight())
        {
            if (this.isConnected(EnumFacing.NORTH) || this.isConnected(EnumFacing.SOUTH))
            {
                parts.add("backZNeg");
                parts.add("barZNeg");
                parts.add("backZPos");
                parts.add("barZPos");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleX");
            }
            else if (this.isConnected(EnumFacing.EAST) || this.isConnected(EnumFacing.WEST))
            {
                parts.add("backXNeg");
                parts.add("barXNeg");
                parts.add("backXPos");
                parts.add("barXPos");

                if (this.isConnected(EnumFacing.DOWN))
                    parts.add("middleZ");
            }
        }

        if (!this.isConnected(EnumFacing.DOWN))
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
        if (this.isServer())
        {
            this.sync();
            return;
        }

        this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
    }

    public boolean isDeadEnd()
    {
        return this.renderConnections.size() <= 1 ||
                this.renderConnections.size() == 2 && this.isConnected(EnumFacing.DOWN);
    }

    public boolean isStraight()
    {
        if (this.renderConnections.size() > 2)
        {
            if (this.renderConnections.size() != 3 || !this.isConnected(EnumFacing.DOWN))
                return false;
        }
        return this.isConnected(EnumFacing.NORTH) && this.isConnected(EnumFacing.SOUTH)
                || this.isConnected(EnumFacing.WEST) && this.isConnected(EnumFacing.EAST);
    }

    public boolean isConnected(final EnumFacing facing)
    {
        return this.renderConnections.contains(facing);
    }

    public NBTTagCompound writeRenderConnections(NBTTagCompound tag)
    {
        for (Map.Entry<EnumFacing, ITileCable<RailGrid>> entry : this.connectionsMap.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        for (Map.Entry<EnumFacing, IRailConnectable> entry : this.adjacentHandler.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    public boolean readRenderConnections(NBTTagCompound tag)
    {
        int previousConnections = this.renderConnections.size();

        this.renderConnections.clear();
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (tag.hasKey("connected" + facing.ordinal()))
                this.renderConnections.add(facing);
        }
        return this.renderConnections.size() != previousConnections;
    }
}
