package nl.odalitadevelopments.menus.contents;

import lombok.AllArgsConstructor;
import nl.odalitadevelopments.menus.contents.placeableitem.PlaceableItemClickAction;
import nl.odalitadevelopments.menus.contents.placeableitem.PlaceableItemDragAction;
import nl.odalitadevelopments.menus.contents.placeableitem.PlaceableItemShiftClickAction;
import nl.odalitadevelopments.menus.listeners.OdalitaEventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@AllArgsConstructor
final class MenuContentsEventsImpl implements MenuContentsEvents {

    private final MenuContentsImpl menuContents;

    @Override
    public void onPlaceableItemClick(@NotNull PlaceableItemClickAction action) {
        if (this.menuContents.menuFrameData() != null) {
            throw new UnsupportedOperationException("Placeable items are not supported in frames.");
        }

        this.menuContents.cache.setPlaceableItemClickAction(action);
    }

    @Override
    public void onPlaceableItemShiftClick(@NotNull PlaceableItemShiftClickAction action) {
        if (this.menuContents.menuFrameData() != null) {
            throw new UnsupportedOperationException("Placeable items are not supported in frames.");
        }

        this.menuContents.cache.setPlaceableItemShiftClickAction(action);
    }

    @Override
    public void onPlaceableItemDrag(@NotNull PlaceableItemDragAction action) {
        if (this.menuContents.menuFrameData() != null) {
            throw new UnsupportedOperationException("Placeable items are not supported in frames.");
        }

        this.menuContents.cache.setPlaceableItemDragAction(action);
    }

    @Override
    public void onPlayerInventoryClick(@NotNull Consumer<@NotNull InventoryClickEvent> eventConsumer) {
        if (this.menuContents.menuFrameData() != null) {
            throw new UnsupportedOperationException("Player inventory click event is not supported in frames.");
        }

        this.menuContents.cache.setPlayerInventoryClickAction(eventConsumer);
    }

    @Override
    public void onClose(boolean beforePlaceableItemRemoveAction, @NotNull Runnable action) {
        if (this.menuContents.menuFrameData() != null) {
            throw new UnsupportedOperationException("Close event is not supported in frames.");
        }

        if (beforePlaceableItemRemoveAction) {
            this.menuContents.cache.setCloseActionBefore(action);
        } else {
            this.menuContents.cache.setCloseActionAfter(action);
        }
    }

    @Override
    public void onClose(@NotNull Runnable action) {
        this.onClose(true, action);
    }

    @Override
    public <T extends Event> @NotNull OdalitaEventListener onEvent(@NotNull Class<T> eventClass, @NotNull Consumer<@NotNull T> eventConsumer, @NotNull EventPriority priority, boolean ignoreCancelled) {
        if (!InventoryEvent.class.isAssignableFrom(eventClass) && !PlayerEvent.class.isAssignableFrom(eventClass)) {
            throw new UnsupportedOperationException("Event class must be assignable from InventoryEvent or PlayerEvent.");
        }

        if (eventClass == InventoryCloseEvent.class) {
            throw new UnsupportedOperationException("Use onClose method to listen for close events.");
        }

        OdalitaEventListener eventListener = ($, event) -> {
            T typedEvent = eventClass.cast(event);
            Inventory sessionInventory = this.menuContents.menuSession.getInventory();

            if (typedEvent instanceof InventoryEvent inventoryEvent) {
                Inventory inventory = inventoryEvent.getInventory();

                // Check if the event is for the current menu
                if (inventory.equals(sessionInventory)) {
                    eventConsumer.accept(typedEvent);
                }
            } else if (typedEvent instanceof PlayerEvent playerEvent) {
                Player player = playerEvent.getPlayer();
                Inventory inventory = player.getOpenInventory().getTopInventory();

                // Check if the event is for the current menu
                if (inventory.equals(sessionInventory)) {
                    eventConsumer.accept(typedEvent);
                }
            }
        };

        Bukkit.getPluginManager().registerEvent(eventClass, eventListener, priority, eventListener, this.menuContents.menuSession.getInstance().getJavaPlugin(), ignoreCancelled);

        this.menuContents.cache.getEventListeners().add(eventListener);
        return eventListener;
    }

    @Override
    public <T extends Event> @NotNull OdalitaEventListener onEvent(@NotNull Class<T> eventClass, @NotNull Consumer<@NotNull T> eventConsumer, @NotNull EventPriority priority) {
        return this.onEvent(eventClass, eventConsumer, priority, false);
    }

    @Override
    public <T extends Event> @NotNull OdalitaEventListener onEvent(@NotNull Class<T> eventClass, @NotNull Consumer<@NotNull T> eventConsumer) {
        return this.onEvent(eventClass, eventConsumer, EventPriority.NORMAL);
    }
}