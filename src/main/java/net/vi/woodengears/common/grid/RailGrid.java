package net.vi.woodengears.common.grid;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.ItemStackMethods;
import net.vi.woodengears.common.grid.logistic.LogisticNetwork;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.ITileNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class RailGrid extends CableGrid
{
    private final LoadingCache<Pair<BlockPos, BlockPos>, Path> pathCache;

    private Map<ITileRail, TileArmReservoir> reservoirMap;
    private Map<ITileRail, TileProvider>     providerMap;
    private Map<ITileRail, TileRequester>    requesterMap;

    @Getter
    private LogisticNetwork<ItemStack> stackNetwork;

    public RailGrid(int identifier)
    {
        super(identifier);

        this.reservoirMap = new HashMap<>();
        this.providerMap = new HashMap<>();
        this.requesterMap = new HashMap<>();

        pathCache = CacheBuilder.newBuilder().maximumSize(100)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Pair<BlockPos, BlockPos>, Path>()
                {
                    @Override
                    public Path load(Pair<BlockPos, BlockPos> key)
                    {
                        return pathFind(key.getKey(), key.getValue());
                    }
                });

        this.stackNetwork = new LogisticNetwork<>(this, ItemStack.class, ItemStackMethods.getInstance());
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.providerMap.isEmpty() || this.reservoirMap.isEmpty() || this.requesterMap.isEmpty())
            return;

        this.stackNetwork.tick();

        if (!this.stackNetwork.getShipments().isEmpty())
        {
            for (LogisticShipment<ItemStack> shipment : this.stackNetwork.getShipments())
            {
                // Ignore warning, this will always return a ITileRail
                TileProvider provider = this.providerMap.get(this.getFromPos(shipment.getFrom()));
                TileRequester requester = this.requesterMap.get(this.getFromPos(shipment.getTo()));

                // TODO : Use requester buffer
                ItemStack shipped = provider.getProvider().fromBuffer(shipment.getContent());

                requester.getRequester().insert(shipped);
            }
            Stream.of(stackNetwork.getShipments().toArray(new LogisticShipment[0])).forEach(this.stackNetwork::completeShipment);
            this.stackNetwork.getShipments().clear();
        }
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new RailGrid(identifier);
    }

    @Override
    public void onMerge(CableGrid grid)
    {
        super.onMerge(grid);

        ((RailGrid) grid).providerMap.forEach(this::addProvider);
        ((RailGrid) grid).requesterMap.forEach(this::addRequester);
        ((RailGrid) grid).reservoirMap.forEach(this::addReservoir);
    }

    @Override
    public void onSplit(CableGrid grid)
    {
        super.onSplit(grid);

        ((RailGrid) grid).providerMap.forEach((rail, provider) ->
        {
            if (this.hasCable(rail))
                this.addProvider(rail, provider);
        });
        ((RailGrid) grid).requesterMap.forEach((rail, requester) ->
        {
            if (this.hasCable(rail))
                this.addRequester(rail, requester);
        });
        ((RailGrid) grid).reservoirMap.forEach((rail, reservoir) ->
        {
            if (this.hasCable(rail))
                this.addReservoir(rail, reservoir);
        });
    }

    public void addReservoir(ITileRail pipe, TileArmReservoir handler)
    {
        if (this.reservoirMap.containsKey(pipe))
            return;
        this.reservoirMap.put(pipe, handler);
    }

    public void removeReservoir(ITileRail pipe)
    {
        this.reservoirMap.remove(pipe);
    }

    public void addProvider(ITileRail pipe, TileProvider handler)
    {
        if (this.providerMap.containsKey(pipe))
            return;
        this.providerMap.put(pipe, handler);
        this.stackNetwork.addProvider(handler.getProvider());
    }

    public void removeProvider(ITileRail pipe)
    {
        TileProvider removed = this.providerMap.remove(pipe);

        if (removed != null)
            this.stackNetwork.removeProvider(removed.getProvider());
    }

    public void addRequester(ITileRail rail, TileRequester requester)
    {
        if (this.requesterMap.containsKey(rail))
            return;
        this.requesterMap.put(rail, requester);
    }

    public void removeRequester(ITileRail rail)
    {
        this.requesterMap.remove(rail);
    }

    @Override
    public boolean removeCable(ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.removeReservoir((ITileRail) cable);
            this.removeProvider((ITileRail) cable);
            this.removeRequester((ITileRail) cable);
            return true;
        }
        return false;
    }

    public int getDistanceBetween(BlockPos from, BlockPos to)
    {
        try
        {
            return this.pathCache.get(Pair.of(from, to)).getPoints().size();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public Path pathFind(BlockPos from, BlockPos to)
    {
        Path path = new Path(from, to);

        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingInt(node ->
        {
            // Manhattan
            if (node.getCost() == -1)
                node.setCost(Math.abs(node.getPos().getX() - to.getX()) + Math.abs(node.getPos().getY() - to.getY()) + Math.abs(node.getPos().getZ() - to.getZ()));
            return node.getCost();
        }));
        HashSet<BlockPos> closedSet = new HashSet<>();

        openSet.add(new PathNode(null, from, this.getFromPos(from)));

        while (!openSet.isEmpty())
        {
            PathNode current = openSet.poll();
            closedSet.add(current.getPos());

            if (current.getPos().equals(to))
            {
                current = current.getPrevious();
                while (current.getPrevious() != null)
                {
                    path.getPoints().add(current.getPos());
                    current = current.getPrevious();
                }
                Collections.reverse(path.getPoints());
                return path;
            }

            for (int edge : current.getTile().getConnections())
            {
                ITileNode tileNode = current.getTile().getConnected(edge);

                if (current.getPrevious() != null && tileNode.getBlockPos().equals(current.getPrevious().getPos()))
                    continue;

                if (!closedSet.contains(tileNode.getBlockPos()))
                    openSet.add(new PathNode(current, tileNode.getBlockPos(), tileNode));
            }
        }
        path.setImpossible(true);
        return path;
    }
}
