package com.myapps.keithpottratz;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Main inventory management activity displaying items in a grid.
 * Supports sorting  and real-time search filtering.
 */
public class InventoryActivity extends AppCompatActivity
        implements AddItemDialogFragment.AddItemListener {

    private static final int REQ_SMS = 1001;
    private static final String PREF_SORT_CRITERIA = "sort_criteria";

    private InventoryAdapter adapter;
    private List<InventoryItem> items;           // All items from database
    private List<InventoryItem> filteredItems;   // Items after search filter
    private RecyclerView recyclerView;

    // DB/DAO
    private AppDatabase db;
    private InventoryDao dao;

    // Sorting state
    private SortCriteria currentSortCriteria = SortCriteria.DATE_ADDED_DESC;

    // Search state
    private String currentSearchQuery = "";
    private boolean isSearchActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);

        db = AppDatabase.getInstance(this);
        dao = db.inventoryDao();

        // Restore saved sort preference
        restoreSortPreference();

        // Build inventory UI (read from DB)
        items = new ArrayList<>(dao.getAll());
        filteredItems = new ArrayList<>(items);

        // Apply saved sort order
        InventorySortManager.sort(filteredItems, currentSortCriteria);

        adapter = new InventoryAdapter(this, filteredItems);

        recyclerView = findViewById(R.id.inventoryGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addItemFab);
        fab.setOnClickListener(v ->
                new AddItemDialogFragment().show(getSupportFragmentManager(), "addItem")
        );

        maybeShowSmsSetup();

        // spacing between cards
        int space = (int) (8 * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        // gentle item entrance the first time
        LayoutAnimationController controller =
                new LayoutAnimationController(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_in), 0.08f);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    // ========================================================================
    // MENU HANDLING
    // ========================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);

        // Set up SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentSearchQuery = newText;
                    applySearchFilter();
                    return true;
                }
            });

            // Track when search is opened/closed
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                    isSearchActive = true;
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                    isSearchActive = false;
                    currentSearchQuery = "";
                    applySearchFilter();
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ========================================================================
    // SORTING FUNCTIONALITY
    // ========================================================================

    /**
     * Shows a dialog with all available sort options.
     */
    private void showSortDialog() {
        String[] options = SortCriteria.getAllDisplayNames();
        int currentIndex = currentSortCriteria.ordinal();

        new AlertDialog.Builder(this)
                .setTitle(R.string.sort_by)
                .setSingleChoiceItems(options, currentIndex, (dialog, which) -> {
                    SortCriteria selected = SortCriteria.values()[which];
                    applySortCriteria(selected);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Applies the selected sort criteria to the inventory list.
     * Shows a toast with the algorithm used and time taken.
     *
     * @param criteria The sort criterion to apply
     */
    private void applySortCriteria(SortCriteria criteria) {
        long startTime = System.nanoTime();

        // Sort the filtered list (what's currently displayed)
        InventorySortManager.sort(filteredItems, criteria);

        // Notify adapter of changes
        adapter.notifyDataSetChanged();

        // Calculate duration
        long durationNanos = System.nanoTime() - startTime;
        long durationMs = durationNanos / 1_000_000;

        // Show feedback with algorithm info
        String algorithm = InventorySortManager.getLastAlgorithmUsed();
        String message = String.format("Sorted by %s in %dms (%s)",
                criteria.getDisplayName(), durationMs, algorithm);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Save preference
        currentSortCriteria = criteria;
        saveSortPreference();
    }

    /**
     * Saves the current sort preference to SharedPreferences.
     */
    private void saveSortPreference() {
        getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .putInt(PREF_SORT_CRITERIA, currentSortCriteria.ordinal())
                .apply();
    }

    /**
     * Restores the sort preference from SharedPreferences.
     */
    private void restoreSortPreference() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        int savedIndex = prefs.getInt(PREF_SORT_CRITERIA, SortCriteria.DATE_ADDED_DESC.ordinal());

        if (savedIndex >= 0 && savedIndex < SortCriteria.values().length) {
            currentSortCriteria = SortCriteria.values()[savedIndex];
        }
    }

    // ========================================================================
    // SEARCH FUNCTIONALITY
    // ========================================================================

    /**
     * Filters the inventory list based on the current search query.
     * Searches in item name and description (case-insensitive).
     */
    private void applySearchFilter() {
        filteredItems.clear();

        if (currentSearchQuery == null || currentSearchQuery.trim().isEmpty()) {
            // No search query - show all items
            filteredItems.addAll(items);
        } else {
            // Filter items matching the query
            String query = currentSearchQuery.toLowerCase().trim();

            for (InventoryItem item : items) {
                boolean matchesName = item.getName() != null &&
                        item.getName().toLowerCase().contains(query);
                boolean matchesDesc = item.getDescription() != null &&
                        item.getDescription().toLowerCase().contains(query);

                if (matchesName || matchesDesc) {
                    filteredItems.add(item);
                }
            }
        }

        // Re-apply current sort order to filtered results
        InventorySortManager.sort(filteredItems, currentSortCriteria);

        // Update UI
        adapter.notifyDataSetChanged();

        // Show feedback if no results
        if (filteredItems.isEmpty() && !currentSearchQuery.isEmpty()) {
            Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
        }
    }

    // ========================================================================
    // LIFECYCLE
    // ========================================================================

    /**
     * Refresh the grid from DB each time we return to this screen.
     * Maintains current sort order and search filter.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Reload from database
        items.clear();
        items.addAll(dao.getAll());

        // Re-apply search filter (which also applies sort)
        applySearchFilter();
    }

    // ========================================================================
    // SMS SETUP
    // ========================================================================

    private void maybeShowSmsSetup() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean seen = prefs.getBoolean("seenSmsScreen", false);

        boolean granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        if (!seen || !granted) {
            startActivityForResult(new Intent(this, SmsNotificationsActivity.class), REQ_SMS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SMS) {
            if (resultCode == RESULT_OK) {
                getSharedPreferences("prefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("seenSmsScreen", true)
                        .apply();
            }
        }
    }

    // ========================================================================
    // ITEM OPERATIONS (CRUD)
    // ========================================================================

    /**
     * From the Add-Item dialog (CREATE)
     * Accepts all item fields: price, category, supplier, and location.
     */
    @Override
    public void onNewItem(String name, String description, int quantity,
                          double price, Long categoryId, Long supplierId, Long locationId) {
        // Create item with all fields using the full constructor
        InventoryItem newItem = new InventoryItem(
                name,
                description,
                quantity,
                categoryId,
                supplierId,
                locationId,
                price,
                null,            // sku (optional)
                10               // default minStockLevel
        );

        long newId = dao.insert(newItem);
        newItem.setId(newId);

        // Refresh in-memory list from DB
        items.clear();
        items.addAll(dao.getAll());

        // Refresh adapter's category cache in case new ones were added
        adapter.refreshCategories();

        // Re-apply search filter and sort
        applySearchFilter();

        // Low-stock alert if quantity < minStockLevel
        if (quantity < newItem.getMinStockLevel()) {
            sendLowStockAlert(newItem);
        }
    }

    /**
     * UPDATE an item (called by adapter)
     */
    public void updateItem(InventoryItem item, int newQuantity, int position) {
        item.setQuantity(newQuantity);
        dao.update(item);

        // Update in both lists
        int masterIndex = items.indexOf(item);
        if (masterIndex >= 0) {
            items.set(masterIndex, item);
        }

        filteredItems.set(position, item);
        adapter.notifyItemChanged(position);

        // Low-stock alert if quantity < 10
        if (newQuantity < 10) {
            sendLowStockAlert(item);
        }
    }

    /**
     * DELETE an item (called by adapter)
     */
    public void deleteItem(InventoryItem item, int position) {
        dao.delete(item);

        // Remove from both lists
        items.remove(item);
        filteredItems.remove(position);
        adapter.notifyItemRemoved(position);
    }

    // ========================================================================
    // SMS HELPER
    // ========================================================================

    private void sendLowStockAlert(InventoryItem item) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String phoneNumber = "1234567890";
            String message = "Low stock alert: " + item.getName() +
                    " has only " + item.getQuantity() + " left.";

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "SMS alert sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ========================================================================
    // ITEM DECORATION
    // ========================================================================

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.set(space, space, space, space);
        }
    }
}
