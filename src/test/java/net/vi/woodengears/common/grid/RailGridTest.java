package net.vi.woodengears.common.grid;

import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.test.GridTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RailGridTest
{
    @Test
    void straightPath()
    {
        RailGrid grid = new RailGrid(GridManager.getInstance().getNextID());

        GridTestBuilder builder = GridTestBuilder.build(grid);
        builder.origin(BlockPos.ORIGIN);

        ITileCable from = builder.northGet();

        builder.south().south().south();

        ITileCable to = builder.southGet();

        builder.create();

        Path path = grid.pathFind(from.getBlockPos(), to.getBlockPos());

        assertThat(path.getFrom()).isEqualTo(from.getBlockPos()).isEqualTo(new BlockPos(0, 0, 0));
        assertThat(path.getTo()).isEqualTo(to.getBlockPos()).isEqualTo(new BlockPos(0, 0, 4));

        assertThat(path.getPoints()).containsExactly(
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, 2),
                new BlockPos(0, 0, 3)
        );
    }

    @Test
    void intersectionPath()
    {
        RailGrid grid = new RailGrid(GridManager.getInstance().getNextID());

        GridTestBuilder builder = GridTestBuilder.build(grid);
        builder.origin(BlockPos.ORIGIN);

        ITileCable from = builder.northGet();

        ITileCable intersection = builder.south().southGet();
        builder.east();
        builder.current(intersection);
        builder.south();

        ITileCable to = builder.southGet();

        builder.create();

        Path path = grid.pathFind(from.getBlockPos(), to.getBlockPos());

        assertThat(path.getFrom()).isEqualTo(from.getBlockPos()).isEqualTo(new BlockPos(0, 0, 0));
        assertThat(path.getTo()).isEqualTo(to.getBlockPos()).isEqualTo(new BlockPos(0, 0, 4));

        assertThat(path.getPoints()).containsExactly(
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, 2),
                new BlockPos(0, 0, 3)
        );
    }

    @Test
    void neighborsPath()
    {
        RailGrid grid = new RailGrid(GridManager.getInstance().getNextID());

        GridTestBuilder builder = GridTestBuilder.build(grid);
        builder.origin(BlockPos.ORIGIN);

        ITileCable from = builder.northGet();
        ITileCable to = builder.southGet();

        builder.create();

        Path path = grid.pathFind(from.getBlockPos(), to.getBlockPos());

        assertThat(path.getFrom()).isEqualTo(from.getBlockPos()).isEqualTo(new BlockPos(0, 0, 0));
        assertThat(path.getTo()).isEqualTo(to.getBlockPos()).isEqualTo(new BlockPos(0, 0, 1));

        assertThat(path.getPoints()).isEmpty();
    }
}

