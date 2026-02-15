# Keith Pottratz Inventory Management

An Android inventory management application built as a capstone project. The app provides user authentication, CRUD inventory operations, SMS low-stock alerts, and advanced sorting/database features.

## Features

- **User Authentication** - Login and account creation with SHA-256 password hashing
- **Inventory Management** - Full CRUD operations for inventory items displayed in a grid layout
- **Item Details** - View and modify individual item information (name, quantity, description, etc.)
- **Low Stock Alerts** - SMS notifications when item quantity falls below a configurable threshold
- **Sorting** - Multiple sorting algorithms (QuickSort, MergeSort, Counting Sort) with smart algorithm selection via `InventorySortManager`
- **Database Relationships** - Normalized schema with categories, suppliers, locations, and inventory history tracking
- **Advanced Queries** - JOIN queries, aggregation, filtering, and transaction support via Room DAOs

## Tech Stack

- **Language**: Java
- **Min SDK**: 34 (Android 14)
- **Target SDK**: 36
- **Database**: Room Persistence Library 2.7.2
- **UI**: Material Design, RecyclerView with GridLayoutManager, View Binding
- **Navigation**: AndroidX Navigation Component
- **Architecture**: MVVM-like with DAO pattern

## Project Structure

```
app/src/main/java/com/myapps/keithpottratz/
├── LoginActivity.java           # Authentication screen (launcher)
├── InventoryActivity.java       # Main inventory grid display
├── ItemDetailActivity.java      # Item view/edit screen
├── SmsNotificationsActivity.java# SMS alert configuration
├── AddItemDialogFragment.java   # Dialog for adding new items
├── InventoryAdapter.java        # RecyclerView adapter for inventory grid
├── InventorySortManager.java    # Sorting algorithm engine
├── SortCriteria.java            # Sort criteria enum
├── SmsNotifier.java             # SMS notification handler
├── AppDatabase.java             # Room database singleton
├── InventoryItem.java           # Inventory entity
├── User.java                    # User entity
├── Category.java                # Category entity
├── Supplier.java                # Supplier entity
├── Location.java                # Location entity
├── InventoryHistory.java        # Audit trail entity
├── InventoryDao.java            # Inventory data access
├── UserDao.java                 # User data access
├── CategoryDao.java             # Category data access
├── SupplierDao.java             # Supplier data access
├── LocationDao.java             # Location data access
├── InventoryHistoryDao.java     # History data access
├── ItemWithCategory.java        # JOIN result model
├── ItemWithSupplier.java        # JOIN result model
├── ItemWithLocation.java        # JOIN result model
├── ItemWithDetails.java         # Full JOIN result model
├── CategoryStats.java           # Aggregation result model
├── SupplierStats.java           # Aggregation result model
└── LowStockItem.java            # Low stock query result model
```

## Database Schema

The app uses a normalized Room database (3NF) with 6 tables:

| Table              | Purpose                        |
|--------------------|--------------------------------|
| `inventory_items`  | Core inventory data            |
| `users`            | User accounts and credentials  |
| `categories`       | Item categorization            |
| `suppliers`        | Supplier information           |
| `locations`        | Storage location tracking      |
| `inventory_history`| Audit trail for item changes   |

## Building

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on a device/emulator running Android 14+

## Permissions

- `SEND_SMS` - Required for low stock SMS notifications

## License

This project was created as an academic capstone project.
