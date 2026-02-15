package com.myapps.keithpottratz;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                InventoryItem.class,
                User.class,
                Category.class,
                Supplier.class,
                Location.class,
                InventoryHistory.class
        },
        version = 2,
        exportSchema = false  // Schema export requires Gradle plugin config
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract InventoryDao inventoryDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract SupplierDao supplierDao();
    public abstract LocationDao locationDao();
    public abstract InventoryHistoryDao inventoryHistoryDao();

    /**
     * Migration from version 1 to version 2.
     *
     * Changes:
     * - Creates new tables: categories, suppliers, locations, inventory_history
     * - Adds new columns to inventory: category_id, supplier_id, location_id,
     *   price, sku, min_stock_level, created_at, updated_at
     * - Adds new column to users: created_at
     * - Renames users.passwordHash to users.password_hash
     * - Creates indices for foreign keys and sku
     * - Inserts default "Uncategorized" category
     * - Preserves ALL existing data
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            long currentTime = System.currentTimeMillis();


            // Create categories table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `categories` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`description` TEXT, " +
                "`color_code` TEXT, " +
                "`created_at` INTEGER NOT NULL)"
            );

            // Create suppliers table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `suppliers` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`contact_person` TEXT, " +
                "`email` TEXT, " +
                "`phone` TEXT, " +
                "`address` TEXT, " +
                "`created_at` INTEGER NOT NULL)"
            );

            // Create locations table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `locations` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`building` TEXT, " +
                "`zone` TEXT, " +
                "`aisle` TEXT, " +
                "`shelf` TEXT, " +
                "`created_at` INTEGER NOT NULL)"
            );

            // Create inventory_history table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `inventory_history` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`item_id` INTEGER NOT NULL, " +
                "`user_id` INTEGER, " +
                "`action` TEXT NOT NULL, " +
                "`field_changed` TEXT, " +
                "`old_value` TEXT, " +
                "`new_value` TEXT, " +
                "`timestamp` INTEGER NOT NULL, " +
                "FOREIGN KEY(`item_id`) REFERENCES `inventory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
            );

            // Create indices for inventory_history
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_history_item_id` ON `inventory_history` (`item_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_history_user_id` ON `inventory_history` (`user_id`)");


            //  Insert default category for existing items


            database.execSQL(
                "INSERT INTO `categories` (`name`, `description`, `color_code`, `created_at`) " +
                "VALUES ('Uncategorized', 'Default category for existing items', '#808080', " + currentTime + ")"
            );

            // Migrate inventory table (add new columns)


            // Add new columns to inventory table
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `category_id` INTEGER");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `supplier_id` INTEGER");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `location_id` INTEGER");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `price` REAL NOT NULL DEFAULT 0.0");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `sku` TEXT");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `min_stock_level` INTEGER NOT NULL DEFAULT 10");
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `created_at` INTEGER NOT NULL DEFAULT " + currentTime);
            database.execSQL("ALTER TABLE `inventory` ADD COLUMN `updated_at` INTEGER NOT NULL DEFAULT " + currentTime);

            // Set all existing items to use the default "Uncategorized" category (id = 1)
            database.execSQL("UPDATE `inventory` SET `category_id` = 1 WHERE `category_id` IS NULL");

            // Create indices for inventory foreign keys
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_category_id` ON `inventory` (`category_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_supplier_id` ON `inventory` (`supplier_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_location_id` ON `inventory` (`location_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_inventory_sku` ON `inventory` (`sku`)");


            //  Migrate users table


            // Rename passwordHash column to password_hash and add created_at
            // SQLite doesn't support RENAME COLUMN in older versions, so we recreate the table

            // Create new users table with updated schema
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `users_new` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`username` TEXT NOT NULL, " +
                "`password_hash` TEXT NOT NULL, " +
                "`created_at` INTEGER NOT NULL)"
            );

            // Copy data from old users table to new one
            database.execSQL(
                "INSERT INTO `users_new` (`id`, `username`, `password_hash`, `created_at`) " +
                "SELECT `id`, `username`, `passwordHash`, " + currentTime + " FROM `users`"
            );

            // Drop old table
            database.execSQL("DROP TABLE `users`");

            // Rename new table to users
            database.execSQL("ALTER TABLE `users_new` RENAME TO `users`");

            // Create unique index on username
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)");

            // ============================================================
            // MIGRATION COMPLETE - All data preserved!
            // ============================================================
        }
    };

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    "inventory.db"
                            )
                            .addMigrations(MIGRATION_1_2)  // Safe migration - preserves data!
                            .allowMainThreadQueries()      // TODO: Remove after updating UI to use background threads
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
