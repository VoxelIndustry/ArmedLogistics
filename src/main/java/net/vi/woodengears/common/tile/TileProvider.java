package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.network.action.ActionSender;
import net.voxelindustry.steamlayer.network.action.IActionReceiver;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

public class TileProvider extends TileLogicisticNode implements ITickable, IActionReceiver
{
    @Getter
    private BaseItemProvider provider;

    private WrappedInventory           wrappedInventory;
    private InventoryBuffer            buffer;

    @Getter
    private BaseProperty<Boolean> whitelistProperty;

    public TileProvider()
    {
        super("provider");

        this.wrappedInventory = new WrappedInventory();
        this.buffer = new InventoryBuffer(8, 8 * 64);

        this.provider = new BaseItemProvider(this, ProviderType.PASSIVE_PROVIDER, this.wrappedInventory, this.buffer);

        this.getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));

        this.whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (!this.buffer.isEmpty())
        {
            list.addText("Buffer:");
            this.buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        this.buffer.writeNBT(tag);

        tag.setBoolean("whitelist", this.whitelistProperty.getValue());
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.buffer.readNBT(tag);
        this.whitelistProperty.setValue(tag.getBoolean("whitelist"));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("provider", player)
                .player(player).inventory(8, 134).hotbar(8, 192)
                .addInventory()
                .syncBooleanValue(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncBooleanValue(whitelistProperty::getValue, whitelistProperty::setValue)
                .syncInventory(this::getConnectedInventory, getCachedInventoryProperty()::setValue, 10)
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : this.buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if("WHITELIST_SWITCH".equals(actionID))
            this.whitelistProperty.setValue(payload.getBoolean("whitelist"));
    }
}
