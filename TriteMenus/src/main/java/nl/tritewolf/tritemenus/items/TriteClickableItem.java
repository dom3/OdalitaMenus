package nl.tritewolf.tritemenus.items;

import lombok.AllArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@AllArgsConstructor
public final class TriteClickableItem implements TriteMenuItem {

    public static TriteClickableItem of(@NotNull ItemStack itemStack, @NotNull Consumer<InventoryClickEvent> clickHandler) {
        return new TriteClickableItem(itemStack, clickHandler);
    }

    private final @NotNull ItemStack itemStack;
    private final @NotNull Consumer<InventoryClickEvent> clickHandler;

    @Override
    public @NotNull ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public @NotNull Consumer<InventoryClickEvent> onClick() {
        return this.clickHandler;
    }
}