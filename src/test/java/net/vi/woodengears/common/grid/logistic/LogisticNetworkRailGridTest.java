package net.vi.woodengears.common.grid.logistic;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.RailGrid;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.test.GridTestBuilder;
import net.vi.woodengears.common.test.TestItemProvider;
import net.vi.woodengears.common.test.TestItemRequester;
import net.vi.woodengears.common.test.WGTestExt;
import net.voxelindustry.steamlayer.grid.GridManager;
import net.voxelindustry.steamlayer.grid.ITileCable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class LogisticNetworkRailGridTest
{
    private GridManager instance;

    @BeforeAll
    void setup()
    {
        this.instance = GridManager.createGetInstance("woodengears:test");
    }
    
    @Test
    void closestProvider()
    {
        // Rails setup
        RailGrid railGrid = new RailGrid(instance.getNextID());

        GridTestBuilder railGridBuilder = GridTestBuilder.build(railGrid).origin(BlockPos.ORIGIN);
        ITileCable requesterRail = railGridBuilder.northGet();

        ITileCable farthestRail = railGridBuilder.east().east().east().east().eastGet();

        railGridBuilder.current(requesterRail).west();

        ITileCable closestRail = railGridBuilder.westGet();

        // Logistic setup
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(railGrid, ItemStack.class,
                ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);

        BaseItemProvider farthestProvider =
                TestItemProvider.build().pos(farthestRail.getBlockPos()).stacks(apple2).create();
        BaseItemProvider closestProvider =
                TestItemProvider.build().pos(closestRail.getBlockPos()).stacks(apple2).create();

        BaseItemRequester requester = new TestItemRequester(requesterRail.getBlockPos());

        grid.addProvider(farthestProvider);
        grid.addProvider(closestProvider);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(Items.APPLE, 2));

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);

        assertThat(order.getShippedParts().get(0).getFrom()).isEqualTo(closestRail.getBlockPos());
    }
}
