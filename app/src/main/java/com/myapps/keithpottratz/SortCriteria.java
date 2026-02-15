package com.myapps.keithpottratz;

/**
 * Enumeration of available sorting criteria for inventory items.
 * Each criterion specifies a display name and sort direction.
 *
 * Used by InventorySortManager to determine how to compare items
 * and by the UI to display sort options to users.
 */
public enum SortCriteria {
    NAME_ASC("Name (A-Z)", true),
    NAME_DESC("Name (Z-A)", false),
    QUANTITY_ASC("Quantity (Low to High)", true),
    QUANTITY_DESC("Quantity (High to Low)", false),
    PRICE_ASC("Price (Low to High)", true),
    PRICE_DESC("Price (High to Low)", false),
    DATE_ADDED_ASC("Oldest First", true),
    DATE_ADDED_DESC("Newest First", false),
    LOW_STOCK_FIRST("Low Stock Priority", true);

    private final String displayName;
    private final boolean ascending;

    /**
     * Constructor for SortCriteria enum values.
     *
     * @param displayName Human-readable name shown in UI
     * @param ascending   True for ascending order, false for descending
     */
    SortCriteria(String displayName, boolean ascending) {
        this.displayName = displayName;
        this.ascending = ascending;
    }

    /**
     * Gets the  display name for this sort criterion.
     *
     * @return Display name suitable for showing in UI dialogs
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this sort criterion uses ascending order.
     *
     * @return True if ascending, false if descending
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Gets all display names as a String array.
     *
     * @return Array of all display names in enum order
     */
    public static String[] getAllDisplayNames() {
        SortCriteria[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }
}
