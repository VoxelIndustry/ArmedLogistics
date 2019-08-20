package net.vi.woodengears.common.grid;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.entity.EntityLogisticArm;
import net.vi.woodengears.common.grid.logistic.ItemStackMethods;
import net.vi.woodengears.common.grid.logistic.LogisticNetwork;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.vi.woodengears.common.tile.TileRequester;
import net.vi.woodengears.common.tile.TileStorage;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.ITileNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RailGrid extends CableGrid
{
    private final LoadingCache<Pair<BlockPos, BlockPos>, Path> pathCache;

    private Map<ITileRail, TileArmReservoir> reservoirMap;
    private Map<ITileRail, TileProvider>     providerMap;
    private Map<ITileRail, TileStorage>      storageMap;
    private Map<ITileRail, TileRequester>    requesterMap;

    @Getter
    private LogisticNetwork<ItemStack> stackNetwork;

    private List<LogisticShipment<ItemStack>> shipped;

    public RailGrid(int identifier)
    {
        super(identifier);

        reservoirMap = new HashMap<>();
        providerMap = new HashMap<>();
        storageMap = new HashMap<>();
        requesterMap = new HashMap<>();

        shipped = new ArrayList<>();

        pathCache = CacheBuilder.newBuilder().maximumSize(100)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(CacheLoader.from(key -> pathFind(key.getKey(), key.getValue())));

        stackNetwork = new LogisticNetwork<>(this, ItemStack.class, ItemStackMethods.getInstance());
    }

    @Override
    public void tick()
    {
        super.tick();

        if (providerMap.isEmpty() || reservoirMap.isEmpty() || (storageMap.isEmpty() && requesterMap.isEmpty()))
            return;

        stackNetwork.tick();

        if (!stackNetwork.getShipments().isEmpty())
        {
            for (LogisticShipment<ItemStack> shipment : stackNetwork.getShipments())
            {
                if (shipped.contains(shipment))
                    continue;

                // Ignore warning, this will always return a ITileRail
                TileProvider provider = providerMap.get(getFromPos(shipment.getFrom()));

                Optional<BlockPos> closestReservoirPos = reservoirMap.keySet().stream().map(ITileNode::getBlockPos).min(Comparator.comparingInt(pos -> getDistanceBetween(pos, shipment.getFrom())));

                if (!closestReservoirPos.isPresent())
                    continue;

                TileArmReservoir reservoir = reservoirMap.get(getFromPos(closestReservoirPos.get()));

                Path reservoirToProvider = getPath(closestReservoirPos.get(), shipment.getFrom());
                Path providerToRequester = getPath(shipment.getFrom(), shipment.getTo());
                Path requesterToReservoir = getPath(shipment.getTo(), closestReservoirPos.get());

                EntityLogisticArm arm = new EntityLogisticArm(provider.getWorld(), shipment, reservoir, reservoirToProvider, providerToRequester, requesterToReservoir);
                provider.getWorld().spawnEntity(arm);

                shipment.setArmID(arm.getUniqueID());
                shipped.add(shipment);
            }
            //  Stream.of(stackNetwork.getShipments().toArray(new LogisticShipment[0])).forEach(this.stackNetwork::completeShipment);
            //  this.stackNetwork.getShipments().clear();
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
        ((RailGrid) grid).storageMap.forEach(this::addStorage);
        ((RailGrid) grid).requesterMap.forEach(this::addRequester);
        ((RailGrid) grid).reservoirMap.forEach(this::addReservoir);
    }

    @Override
    public void onSplit(CableGrid grid)
    {
        super.onSplit(grid);

        ((RailGrid) grid).providerMap.forEach((rail, provider) ->
        {
            if (hasCable(rail))
                addProvider(rail, provider);
        });
        ((RailGrid) grid).storageMap.forEach((rail, storage) ->
        {
            if (hasCable(rail))
                addStorage(rail, storage);
        });
        ((RailGrid) grid).requesterMap.forEach((rail, requester) ->
        {
            if (hasCable(rail))
                addRequester(rail, requester);
        });
        ((RailGrid) grid).reservoirMap.forEach((rail, reservoir) ->
        {
            if (hasCable(rail))
                addReservoir(rail, reservoir);
        });
    }

    public void addReservoir(ITileRail pipe, TileArmReservoir handler)
    {
        if (reservoirMap.containsKey(pipe))
            return;
        reservoirMap.put(pipe, handler);
    }

    public void removeReservoir(ITileRail pipe)
    {
        reservoirMap.remove(pipe);
    }

    public void addProvider(ITileRail pipe, TileProvider handler)
    {
        if (providerMap.containsKey(pipe))
            return;
        providerMap.put(pipe, handler);
        stackNetwork.addProvider(handler.getProvider());
    }

    public void removeProvider(ITileRail pipe)
    {
        TileProvider removed = providerMap.remove(pipe);

        if (removed != null)
            stackNetwork.removeProvider(removed.getProvider());
    }

    public void addRequester(ITileRail rail, TileRequester requester)
    {
        if (requesterMap.containsKey(rail))
            return;
        requesterMap.put(rail, requester);
    }

    public void removeRequester(ITileRail rail)
    {
        requesterMap.remove(rail);
    }

    public void addStorage(ITileRail pipe, TileStorage handler)
    {
        if (storageMap.containsKey(pipe))
            return;
        storageMap.put(pipe, handler);
        stackNetwork.addProvider(handler.getProvider());
    }

    public void removeStorage(ITileRail pipe)
    {
        TileStorage removed = storageMap.remove(pipe);

        if (removed != null)
            stackNetwork.removeProvider(removed.getProvider());
    }

    @Override
    public boolean removeCable(ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            removeReservoir((ITileRail) cable);
            removeProvider((ITileRail) cable);
            removeRequester((ITileRail) cable);
            removeStorage((ITileRail) cable);
            return true;
        }
        return false;
    }

    public int getDistanceBetween(BlockPos from, BlockPos to)
    {
        try
        {
            return pathCache.get(Pair.of(from, to)).getPoints().size();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public Path getPath(BlockPos from, BlockPos to)
    {
        try
        {
            return pathCache.get(Pair.of(from, to));
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
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

        openSet.add(new PathNode(null, from, getFromPos(from)));

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
                path.getPoints().add(to);
                path.getPoints().add(0, from);
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
