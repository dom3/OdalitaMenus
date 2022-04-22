package nl.tritewolf.tritemenus.contents.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.tritewolf.tritemenus.contents.InventoryContents;
import nl.tritewolf.tritemenus.items.MenuItem;
import nl.tritewolf.tritemenus.iterators.MenuIterator;

import java.util.List;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Pagination {

    private final String id;
    private final InventoryContents contents;
    private final int itemsPerPage;
    private final MenuIterator iterator;
    private final List<Supplier<MenuItem>> items;

    @Setter
    private int currentPage = 0;
    @Setter
    private volatile boolean initialized = false;

    public boolean isOnPage(int index) {
        return index >= this.currentPage * this.itemsPerPage && index < (this.currentPage + 1) * this.itemsPerPage;
    }

    public List<Supplier<MenuItem>> getItemsOnPage() {
        return this.items.subList(
                Math.max(0, this.currentPage * this.itemsPerPage),
                Math.min(this.items.size() - 1, (this.currentPage + 1) * this.itemsPerPage)
        );
    }

    public boolean isFirst() {
        return this.currentPage == 0;
    }

    public boolean isLast() {
        int pageCount = (int) Math.ceil((double) this.items.size() / this.itemsPerPage);
        return this.currentPage >= pageCount - 1;
    }

    public synchronized Pagination addItem(Supplier<MenuItem> menuItemSupplier) {
        if (this.initialized && this.isOnPage(this.items.size())) {
            this.iterator.setAsync(menuItemSupplier.get(), false).next();
        }

        this.items.add(menuItemSupplier);
        return this;
    }
}