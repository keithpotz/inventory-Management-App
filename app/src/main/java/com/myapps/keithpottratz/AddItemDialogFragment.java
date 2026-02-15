package com.myapps.keithpottratz;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.myapps.keithpottratz.databinding.DialogAddItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog fragment for adding new inventory items.
 * Collects item name, description, price, category, supplier, location, and quantity.
 */
public class AddItemDialogFragment extends DialogFragment {

    /**
     * Interface for communicating new item data back to the host activity.
     */
    public interface AddItemListener {
        void onNewItem(String name, String description, int quantity,
                       double price, Long categoryId, Long supplierId, Long locationId);
    }

    private DialogAddItemBinding binding;
    private int currentQty = 0;

    // DAOs
    private CategoryDao categoryDao;
    private SupplierDao supplierDao;
    private LocationDao locationDao;

    // Data lists
    private List<Category> categories = new ArrayList<>();
    private List<Supplier> suppliers = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();

    // Selected IDs
    private Long selectedCategoryId = null;
    private Long selectedSupplierId = null;
    private Long selectedLocationId = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate with View Binding
        binding = DialogAddItemBinding.inflate(LayoutInflater.from(getContext()));

        // Get database access
        AppDatabase db = AppDatabase.getInstance(requireContext());
        categoryDao = db.categoryDao();
        supplierDao = db.supplierDao();
        locationDao = db.locationDao();

        // Load data
        loadCategories();
        loadSuppliers();
        loadLocations();

        // Set up UI components
        setupQuantityControls();
        setupCategorySpinner();
        setupSupplierSpinner();
        setupLocationSpinner();
        setupAddButtons();
        setupSaveButton();

        // Cancel just closes
        binding.cancelNewItemButton.setOnClickListener(v -> dismiss());

        // Build the AlertDialog using our custom view
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_item)
                .setView(binding.getRoot())
                .create();
    }

    // ==================== DATA LOADING ====================

    private void loadCategories() {
        categories = new ArrayList<>();
        categories.addAll(categoryDao.getAll());
    }

    private void loadSuppliers() {
        suppliers = new ArrayList<>();
        suppliers.addAll(supplierDao.getAll());
    }

    private void loadLocations() {
        locations = new ArrayList<>();
        locations.addAll(locationDao.getAll());
    }

    // ==================== QUANTITY CONTROLS ====================

    private void setupQuantityControls() {
        binding.increaseQty.setOnClickListener(v -> {
            currentQty++;
            binding.currentQty.setText(String.valueOf(currentQty));
        });

        binding.decreaseQty.setOnClickListener(v -> {
            if (currentQty > 0) {
                currentQty--;
                binding.currentQty.setText(String.valueOf(currentQty));
            }
        });
    }

    // ==================== CATEGORY SPINNER ====================

    private void setupCategorySpinner() {
        List<String> names = new ArrayList<>();
        names.add(getString(R.string.no_category));
        for (Category cat : categories) {
            names.add(cat.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(adapter);

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = (position == 0) ? null : categories.get(position - 1).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = null;
            }
        });
    }

    // ==================== SUPPLIER SPINNER ====================

    private void setupSupplierSpinner() {
        List<String> names = new ArrayList<>();
        names.add(getString(R.string.no_supplier));
        for (Supplier sup : suppliers) {
            names.add(sup.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.supplierSpinner.setAdapter(adapter);

        binding.supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSupplierId = (position == 0) ? null : suppliers.get(position - 1).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSupplierId = null;
            }
        });
    }

    // ==================== LOCATION SPINNER ====================

    private void setupLocationSpinner() {
        List<String> names = new ArrayList<>();
        names.add(getString(R.string.no_location));
        for (Location loc : locations) {
            names.add(loc.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.locationSpinner.setAdapter(adapter);

        binding.locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocationId = (position == 0) ? null : locations.get(position - 1).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLocationId = null;
            }
        });
    }

    // ==================== ADD NEW BUTTONS ====================

    private void setupAddButtons() {
        binding.addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
        binding.addSupplierButton.setOnClickListener(v -> showAddSupplierDialog());
        binding.addLocationButton.setOnClickListener(v -> showAddLocationDialog());
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.enter_category_name);
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_new_category)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        addNewCategory(name);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAddSupplierDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.enter_supplier_name);
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_new_supplier)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        addNewSupplier(name);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAddLocationDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.enter_location_name);
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_new_location)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        addNewLocation(name);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // ==================== ADD NEW RECORDS ====================

    private void addNewCategory(String name) {
        Category existing = categoryDao.getByName(name);
        if (existing != null) {
            Toast.makeText(requireContext(),
                    "Category '" + name + "' already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Category newItem = new Category(name, null, null);
        long newId = categoryDao.insert(newItem);
        newItem.setId(newId);
        categories.add(newItem);

        setupCategorySpinner();
        binding.categorySpinner.setSelection(categories.size());

        Toast.makeText(requireContext(), R.string.category_added, Toast.LENGTH_SHORT).show();
    }

    private void addNewSupplier(String name) {
        Supplier existing = supplierDao.getByName(name);
        if (existing != null) {
            Toast.makeText(requireContext(),
                    "Supplier '" + name + "' already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Supplier newItem = new Supplier(name, null, null, null, null);
        long newId = supplierDao.insert(newItem);
        newItem.setId(newId);
        suppliers.add(newItem);

        setupSupplierSpinner();
        binding.supplierSpinner.setSelection(suppliers.size());

        Toast.makeText(requireContext(), R.string.supplier_added, Toast.LENGTH_SHORT).show();
    }

    private void addNewLocation(String name) {
        Location existing = locationDao.getByName(name);
        if (existing != null) {
            Toast.makeText(requireContext(),
                    "Location '" + name + "' already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Location newItem = new Location(name, null, null, null, null);
        long newId = locationDao.insert(newItem);
        newItem.setId(newId);
        locations.add(newItem);

        setupLocationSpinner();
        binding.locationSpinner.setSelection(locations.size());

        Toast.makeText(requireContext(), R.string.location_added, Toast.LENGTH_SHORT).show();
    }

    // ==================== SAVE BUTTON ====================

    private void setupSaveButton() {
        binding.saveNewItemButton.setOnClickListener(v -> {
            // Get values
            String name = binding.newName.getText().toString().trim();
            String desc = binding.newDescription.getText().toString().trim();
            String priceStr = binding.newPrice.getText().toString().trim();

            // Validate name
            if (name.isEmpty()) {
                binding.newName.setError(getString(R.string.name) + " required");
                binding.newName.requestFocus();
                return;
            }

            // Parse price
            double price = 0.0;
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                    if (price < 0) {
                        binding.newPrice.setError("Price cannot be negative");
                        binding.newPrice.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.newPrice.setError("Invalid price");
                    binding.newPrice.requestFocus();
                    return;
                }
            }

            // Notify host activity
            if (getActivity() instanceof AddItemListener) {
                ((AddItemListener) getActivity())
                        .onNewItem(name, desc, currentQty, price,
                                selectedCategoryId, selectedSupplierId, selectedLocationId);
            }

            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
