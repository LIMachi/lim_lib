package com.limachi.lim_lib.menus.slots;

import com.limachi.lim_lib.utils.FluidItem;
import com.limachi.lim_lib.utils.SimpleTank;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public class TankSlot extends Slot implements IFluidHandler {
    protected Function<TankSlot, Boolean> isActive;
    Supplier<IFluidTank> tank;

    public TankSlot(int capacity, int xPosition, int yPosition, Function<TankSlot, Boolean> isActive) {
        super(new SimpleContainer(0), 0, xPosition, yPosition);
        final SimpleTank inner = new SimpleTank(capacity);
        tank = ()->inner;
        this.isActive = isActive;
    }

    public TankSlot(Supplier<IFluidTank> tank, int xPosition, int yPosition, Function<TankSlot, Boolean> isActive) {
        super(new SimpleContainer(0), 0, xPosition, yPosition);
        this.tank = tank;
        this.isActive = isActive;
    }

    public IFluidTank getTankHandler() { return tank.get(); }

    @Nonnull
    @Override
    public ItemStack getItem() {
        FluidStack fluid = getFluid();
        if (fluid.isEmpty())
            return ItemStack.EMPTY;
        return FluidItem.fromFluid(fluid);
    }

    @Override
    public void set(ItemStack stack) {
        if (stack.getItem() instanceof FluidItem) {
            FluidStack fluid = FluidItem.getHandler(stack).getFluidInTank(0);
            FluidStack local = getTankHandler().getFluid();
            if (fluid.isFluidEqual(local)) {
                if (fluid.getAmount() > local.getAmount()) {
                    fluid.setAmount(fluid.getAmount() - local.getAmount());
                    getTankHandler().fill(fluid, FluidAction.EXECUTE);
                } else if (fluid.getAmount() < local.getAmount())
                    getTankHandler().drain(local.getAmount() - fluid.getAmount(), FluidAction.EXECUTE);
            } else {
                if (!local.isEmpty())
                    getTankHandler().drain(local.getAmount(), FluidAction.EXECUTE);
                if (!fluid.isEmpty())
                    getTankHandler().fill(fluid, FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public boolean hasItem() { return isActive() && getTankHandler().getFluidAmount() > 0; }

    @Override
    public boolean isActive() { return isActive.apply(this); }

    @Override
    public boolean isHighlightable() { return isActive(); }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) { return false; }

    @Override
    public boolean mayPickup(@Nonnull Player playerIn) { return false; }

    @Override
    @Nonnull
    public ItemStack remove(int amount) { return ItemStack.EMPTY; }

    public FluidStack getFluid() { return getTankHandler().getFluid(); }

    public int getCapacity() { return getTankHandler().getCapacity(); }

    @Override
    public int getTanks() { return isActive() ? 1 : 0; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) { return getTankHandler().getFluid(); }

    @Override
    public int getTankCapacity(int tank) { return getTankHandler().getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return getTankHandler().isFluidValid(stack); }

    @Override
    public int fill(FluidStack resource, FluidAction action) { return getTankHandler().fill(resource, action); }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return getTankHandler().drain(resource, action); }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return getTankHandler().drain(maxDrain, action); }
}
