package nl.tritewolf.tritemenus.items;

import lombok.AllArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@AllArgsConstructor
public final class TriteUpdatableItem implements TriteMenuItem {

    public static TriteUpdatableItem of(@NotNull Supplier<ItemStack> itemStackSupplier, @NotNull Consumer<InventoryClickEvent> clickHandler, int updateTicks) {
        return new TriteUpdatableItem(itemStackSupplier, clickHandler, updateTicks);
    }

    public static TriteUpdatableItem of(@NotNull Supplier<ItemStack> itemStackSupplier, @NotNull Consumer<InventoryClickEvent> clickHandler) {
        return new TriteUpdatableItem(itemStackSupplier, clickHandler);
    }

    public static TriteUpdatableItem of(@NotNull Supplier<ItemStack> itemStackSupplier, int updateTicks) {
        return new TriteUpdatableItem(itemStackSupplier, updateTicks);
    }

    public static TriteUpdatableItem of(@NotNull Supplier<ItemStack> itemStackSupplier) {
        return new TriteUpdatableItem(itemStackSupplier);
    }

    private final @NotNull Supplier<ItemStack> itemStackSupplier;
    private final @Nullable Consumer<InventoryClickEvent> clickHandler;
    private final int updateTicks;

    public TriteUpdatableItem(@NotNull Supplier<ItemStack> itemStackSupplier, @NotNull Consumer<InventoryClickEvent> clickHandler) {
        this(itemStackSupplier, clickHandler, 20); // 20 update ticks = 1 second
    }

    public TriteUpdatableItem(@NotNull Supplier<ItemStack> itemStackSupplier, int updateTicks) {
        this(itemStackSupplier, null, updateTicks);
    }

    public TriteUpdatableItem(@NotNull Supplier<ItemStack> itemStackSupplier) {
        this(itemStackSupplier, null, 20); // 20 update ticks = 1 second
    }

    public @NotNull ItemStack getItemStack() {
        return this.itemStackSupplier.get();
    }

    @Override
    public @NotNull Consumer<InventoryClickEvent> onClick() {
        return (this.clickHandler == null) ? (event) -> {} : this.clickHandler;
    }

    @Override
    public boolean isUpdatable() {
        return true;
    }

    @Override
    public int getUpdateTicks() {
        return this.updateTicks;
    }
}