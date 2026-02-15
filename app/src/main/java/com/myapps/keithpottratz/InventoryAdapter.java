package com.myapps.keithpottratz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * RecyclerView adapter for displaying inventory items in a grid.
 * Shows item name, quantity, price, and category.
 */
public class InventoryAdapter
        extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final List<InventoryItem> items;
    private final Context context;
    private final AppDatabase db;
    private final Map<Long, String> categoryCache = new HashMap<>();
    private final Map<Long, String> supplierCache = new HashMap<>();
    private final Map<Long, String> locationCache = new HashMap<>();
    private final NumberFormat currencyFormat;

    public InventoryAdapter(Context context, List<InventoryItem> items) {
        this.context = context;
        this.items = items;
        this.db = AppDatabase.getInstance(context);
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        // Pre-load all reference data for efficiency
        loadCategories();
        loadSuppliers();
        loadLocations();
    }

    /**
     * Load all categories into cache for quick lookup.
     */
    private void loadCategories() {
        categoryCache.clear();
        List<Category> categories = db.categoryDao().getAll();
        for (Category cat : categories) {
            categoryCache.put(cat.getId(), cat.getName());
        }
    }

    /**
     * Load all suppliers into cache for quick lookup.
     */
    private void loadSuppliers() {
        supplierCache.clear();
        List<Supplier> suppliers = db.supplierDao().getAll();
        for (Supplier sup : suppliers) {
            supplierCache.put(sup.getId(), sup.getName());
        }
    }

    /**
     * Load all locations into cache for quick lookup.
     */
    private void loadLocations() {
        locationCache.clear();
        List<Location> locations = db.locationDao().getAll();
        for (Location loc : locations) {
            locationCache.put(loc.getId(), loc.getName());
        }
    }

    /**
     * Refresh all caches (call when reference data changes).
     */
    public void refreshCategories() {
        loadCategories();
        loadSuppliers();
        loadLocations();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_inventory_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = items.get(position);

        // Bind basic info
        holder.name.setText(item.getName());
        holder.qty.setText("Qty: " + item.getQuantity());

        // Bind price
        holder.price.setText(currencyFormat.format(item.getPrice()));

        // Build category chip text (can include supplier/location)
        String chipText = buildChipText(item);
        if (chipText != null && !chipText.isEmpty()) {
            holder.category.setText(chipText);
            holder.category.setVisibility(View.VISIBLE);
        } else {
            holder.category.setVisibility(View.GONE);
        }

        // Navigate to detail when tapped
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemDetailActivity.class);
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.getId());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_NAME, item.getName());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_DESC, item.getDescription());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_QTY, item.getQuantity());
            v.getContext().startActivity(intent);
        });

        // Increment quantity by 1 on long click
        holder.itemView.setOnLongClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                InventoryItem target = items.get(pos);
                int newQty = target.getQuantity() + 1;

                if (context instanceof InventoryActivity) {
                    ((InventoryActivity) context).updateItem(target, newQty, pos);
                }
            }
            return true; // consumed
        });

        // DELETE from DB and UI
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                InventoryItem target = items.get(pos);

                if (context instanceof InventoryActivity) {
                    ((InventoryActivity) context).deleteItem(target, pos);
                }
            }
        });
    }

    /**
     * Build the text for the category chip.
     * Shows category name, or supplier/location if no category.
     */
    private String buildChipText(InventoryItem item) {
        // Priority: Category > Supplier > Location
        Long categoryId = item.getCategoryId();
        if (categoryId != null && categoryCache.containsKey(categoryId)) {
            return categoryCache.get(categoryId);
        }

        Long supplierId = item.getSupplierId();
        if (supplierId != null && supplierCache.containsKey(supplierId)) {
            return "📦 " + supplierCache.get(supplierId);
        }

        Long locationId = item.getLocationId();
        if (locationId != null && locationCache.containsKey(locationId)) {
            return "📍 " + locationCache.get(locationId);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, qty, price;
        final Chip category;
        final ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardItemName);
            qty = itemView.findViewById(R.id.cardItemQty);
            price = itemView.findViewById(R.id.cardItemPrice);
            category = itemView.findViewById(R.id.cardItemCategory);
            deleteButton = itemView.findViewById(R.id.deleteItem);
        }
    }
}
