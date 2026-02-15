package com.myapps.keithpottratz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sorting manager for inventory items implementing multiple sorting algorithms.
 *
 * This class provides a smart sorting system that automatically selects the
 * optimal algorithm based on:
 * - Dataset size (InsertionSort for small datasets)
 * - Sort criteria (CountingSort for quantity-based sorting)
 * - General case (QuickSort for most scenarios)
 *
 * Algorithms implemented:
 * - QuickSort: O(n log n) average, best general-purpose algorithm
 * - MergeSort: O(n log n) guaranteed, stable sort
 * - CountingSort: O(n + k) for integer quantities, very fast
 * - InsertionSort: O(n^2) but efficient for small datasets (<50 items)
 *
 * @author Keith Pottratz
 * @version 1.0
 */
public class InventorySortManager {

    // Threshold for using InsertionSort on small datasets
    private static final int INSERTION_SORT_THRESHOLD = 50;

    // Maximum quantity range for using CountingSort
    private static final int COUNTING_SORT_MAX_RANGE = 100000;

    // Track which algorithm was used (for performance display)
    private static String lastAlgorithmUsed = "";

    /**
     * Private constructor - all methods are static.
     */
    private InventorySortManager() {
    }

    /**
     * Main entry point for sorting inventory items.
     * Automatically selects the optimal algorithm based on data characteristics.
     *
     * Algorithm Selection Logic:
     * 1. If dataset < 50 items: Use InsertionSort (low overhead)
     * 2. If sorting by quantity and range < 100,000: Use CountingSort (linear time)
     * 3. Otherwise: Use QuickSort (best general-purpose)
     *
     * @param items    List of inventory items to sort (modified in-place)
     * @param criteria The sorting criterion to use
     */
    public static void sort(List<InventoryItem> items, SortCriteria criteria) {
        if (items == null || items.size() <= 1) {
            lastAlgorithmUsed = "None (trivial)";
            return;
        }

        int size = items.size();

        // Small dataset: use InsertionSort (low overhead, cache-friendly)
        if (size < INSERTION_SORT_THRESHOLD) {
            lastAlgorithmUsed = "InsertionSort";
            insertionSort(items, criteria);
            return;
        }

        // Quantity sorting: try CountingSort for linear time performance
        if (criteria == SortCriteria.QUANTITY_ASC ||
            criteria == SortCriteria.QUANTITY_DESC) {

            int maxQuantity = findMaxQuantity(items);

            // Use CountingSort if range is reasonable
            if (maxQuantity < COUNTING_SORT_MAX_RANGE) {
                lastAlgorithmUsed = "CountingSort";
                countingSort(items, criteria);
                return;
            }
        }

        // Default: QuickSort for everything else
        lastAlgorithmUsed = "QuickSort";
        quickSort(items, 0, items.size() - 1, criteria);
    }

    /**
     * Gets the name of the algorithm used in the last sort operation.
     *
     * @return Algorithm name (e.g., "QuickSort", "CountingSort")
     */
    public static String getLastAlgorithmUsed() {
        return lastAlgorithmUsed;
    }

    // ========================================================================
    // QUICKSORT IMPLEMENTATION
    // Time Complexity: O(n log n) average, O(n^2) worst case
    // Space Complexity: O(log n) for recursion stack
    // ========================================================================

    /**
     * QuickSort algorithm using divide-and-conquer approach.
     * Uses middle element as pivot to avoid worst-case on sorted data.
     *
     * @param items    List to sort
     * @param low      Start index of partition
     * @param high     End index of partition
     * @param criteria Sort criterion for comparisons
     */
    private static void quickSort(List<InventoryItem> items,
                                  int low,
                                  int high,
                                  SortCriteria criteria) {
        if (low < high) {
            // Partition and get pivot index
            int pivotIndex = partition(items, low, high, criteria);

            // Recursively sort elements before and after partition
            quickSort(items, low, pivotIndex - 1, criteria);
            quickSort(items, pivotIndex + 1, high, criteria);
        }
    }

    /**
     * Partition helper for QuickSort.
     * Selects middle element as pivot and partitions array around it.
     *
     * @param items    List to partition
     * @param low      Start index
     * @param high     End index
     * @param criteria Sort criterion
     * @return Final position of pivot element
     */
    private static int partition(List<InventoryItem> items,
                                 int low,
                                 int high,
                                 SortCriteria criteria) {
        // Choose middle element as pivot (avoids O(n^2) on sorted data)
        int middle = low + (high - low) / 2;
        InventoryItem pivot = items.get(middle);

        // Move pivot to end temporarily
        Collections.swap(items, middle, high);

        // Partition index
        int i = low - 1;

        // Scan through array, moving smaller elements to left
        for (int j = low; j < high; j++) {
            if (compare(items.get(j), pivot, criteria) <= 0) {
                i++;
                Collections.swap(items, i, j);
            }
        }

        // Place pivot in its final position
        Collections.swap(items, i + 1, high);

        return i + 1;
    }

    // ========================================================================
    // MERGESORT IMPLEMENTATION
    // Time Complexity: O(n log n) guaranteed
    // Space Complexity: O(n) for temporary arrays
    // ========================================================================

    /**
     * MergeSort algorithm - stable sort with guaranteed O(n log n) performance.
     * Not used by default but available for cases requiring stable sorting.
     *
     * @param items    List to sort
     * @param criteria Sort criterion
     */
    public static void mergeSort(List<InventoryItem> items, SortCriteria criteria) {
        if (items == null || items.size() <= 1) {
            return;
        }
        lastAlgorithmUsed = "MergeSort";
        mergeSortRecursive(items, criteria);
    }

    /**
     * Recursive MergeSort implementation.
     */
    private static void mergeSortRecursive(List<InventoryItem> items,
                                           SortCriteria criteria) {
        if (items.size() <= 1) {
            return;
        }

        int mid = items.size() / 2;

        // Split into left and right halves
        List<InventoryItem> left = new ArrayList<>(items.subList(0, mid));
        List<InventoryItem> right = new ArrayList<>(items.subList(mid, items.size()));

        // Recursively sort each half
        mergeSortRecursive(left, criteria);
        mergeSortRecursive(right, criteria);

        // Merge sorted halves back together
        merge(items, left, right, criteria);
    }

    /**
     * Merge two sorted lists into one.
     *
     * @param result   Target list to merge into
     * @param left     Left sorted half
     * @param right    Right sorted half
     * @param criteria Sort criterion
     */
    private static void merge(List<InventoryItem> result,
                              List<InventoryItem> left,
                              List<InventoryItem> right,
                              SortCriteria criteria) {
        int i = 0, j = 0, k = 0;

        // Merge while both lists have elements
        while (i < left.size() && j < right.size()) {
            if (compare(left.get(i), right.get(j), criteria) <= 0) {
                result.set(k++, left.get(i++));
            } else {
                result.set(k++, right.get(j++));
            }
        }

        // Copy remaining elements from left
        while (i < left.size()) {
            result.set(k++, left.get(i++));
        }

        // Copy remaining elements from right
        while (j < right.size()) {
            result.set(k++, right.get(j++));
        }
    }

    // ========================================================================
    // COUNTING SORT IMPLEMENTATION
    // Time Complexity: O(n + k) where k is the range of values
    // Space Complexity: O(k) for bucket array
    // Best for: Sorting by quantity when range is reasonable
    // ========================================================================

    /**
     * CountingSort algorithm for quantity-based sorting.
     * Achieves linear time by using buckets instead of comparisons.
     *
     * Only works for QUANTITY_ASC and QUANTITY_DESC criteria.
     *
     * @param items    List to sort
     * @param criteria Must be QUANTITY_ASC or QUANTITY_DESC
     */
    private static void countingSort(List<InventoryItem> items,
                                     SortCriteria criteria) {
        if (items.isEmpty()) {
            return;
        }

        // Find maximum quantity to determine bucket count
        int maxQuantity = findMaxQuantity(items);

        // Create buckets for each possible quantity value (0 to max)
        List<List<InventoryItem>> buckets = new ArrayList<>(maxQuantity + 1);
        for (int i = 0; i <= maxQuantity; i++) {
            buckets.add(new ArrayList<>());
        }

        // Place each item in its corresponding bucket
        for (InventoryItem item : items) {
            int quantity = item.getQuantity();
            // Handle negative quantities (shouldn't happen, but be safe)
            if (quantity < 0) {
                quantity = 0;
            }
            buckets.get(quantity).add(item);
        }

        // Rebuild list from buckets in sorted order
        items.clear();

        if (criteria == SortCriteria.QUANTITY_ASC) {
            // Ascending: iterate buckets from 0 to max
            for (int i = 0; i <= maxQuantity; i++) {
                items.addAll(buckets.get(i));
            }
        } else {
            // Descending: iterate buckets from max to 0
            for (int i = maxQuantity; i >= 0; i--) {
                items.addAll(buckets.get(i));
            }
        }
    }

    /**
     * Find the maximum quantity in the item list.
     */
    private static int findMaxQuantity(List<InventoryItem> items) {
        int max = 0;
        for (InventoryItem item : items) {
            if (item.getQuantity() > max) {
                max = item.getQuantity();
            }
        }
        return max;
    }

    // ========================================================================
    // INSERTION SORT IMPLEMENTATION
    // Time Complexity: O(n^2) worst case, but very fast for small n
    // Space Complexity: O(1)
    // Best for: Small datasets (<50 items) due to low overhead
    // ========================================================================

    /**
     * InsertionSort algorithm for small datasets.
     * Despite O(n^2) complexity, it's faster than QuickSort for small n
     * due to low overhead and cache efficiency.
     *
     * @param items    List to sort
     * @param criteria Sort criterion
     */
    private static void insertionSort(List<InventoryItem> items,
                                      SortCriteria criteria) {
        for (int i = 1; i < items.size(); i++) {
            InventoryItem key = items.get(i);
            int j = i - 1;

            // Shift elements that are greater than key
            while (j >= 0 && compare(items.get(j), key, criteria) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }

            // Place key in its correct position
            items.set(j + 1, key);
        }
    }

    // ========================================================================
    // COMPARISON HELPER
    // Handles all sort criteria with proper null safety
    // ========================================================================

    /**
     * Compare two inventory items based on the specified sort criterion.
     *
     * @param a        First item
     * @param b        Second item
     * @param criteria Sort criterion determining comparison field
     * @return Negative if a < b, zero if equal, positive if a > b
     */
    private static int compare(InventoryItem a,
                               InventoryItem b,
                               SortCriteria criteria) {
        int result;

        switch (criteria) {
            case NAME_ASC:
            case NAME_DESC:
                // Null-safe string comparison
                String nameA = a.getName() != null ? a.getName() : "";
                String nameB = b.getName() != null ? b.getName() : "";
                result = nameA.compareToIgnoreCase(nameB);
                break;

            case QUANTITY_ASC:
            case QUANTITY_DESC:
                result = Integer.compare(a.getQuantity(), b.getQuantity());
                break;

            case PRICE_ASC:
            case PRICE_DESC:
                result = Double.compare(a.getPrice(), b.getPrice());
                break;

            case DATE_ADDED_ASC:
            case DATE_ADDED_DESC:
                result = Long.compare(a.getCreatedAt(), b.getCreatedAt());
                break;

            case LOW_STOCK_FIRST:
                // Items with larger deficit (minStockLevel - quantity) come first
                // Deficit = how much below minimum stock level
                int deficitA = a.getMinStockLevel() - a.getQuantity();
                int deficitB = b.getMinStockLevel() - b.getQuantity();
                // Higher deficit = more urgent = should come first
                result = Integer.compare(deficitB, deficitA);
                break;

            default:
                result = 0;
        }

        // Flip result for descending sorts
        if (!criteria.isAscending()) {
            result = -result;
        }

        return result;
    }

    // ========================================================================
    // UTILITY METHODS FOR TESTING AND BENCHMARKING
    // ========================================================================

    /**
     * Force use of a specific algorith.
     *
     * @param items     List to sort
     * @param criteria  Sort criterion
     * @param algorithm Algorithm name: "quick", "merge", "counting", "insertion"
     */
    public static void sortWithAlgorithm(List<InventoryItem> items,
                                         SortCriteria criteria,
                                         String algorithm) {
        if (items == null || items.size() <= 1) {
            return;
        }

        switch (algorithm.toLowerCase()) {
            case "quick":
            case "quicksort":
                lastAlgorithmUsed = "QuickSort (forced)";
                quickSort(items, 0, items.size() - 1, criteria);
                break;

            case "merge":
            case "mergesort":
                lastAlgorithmUsed = "MergeSort (forced)";
                mergeSortRecursive(items, criteria);
                break;

            case "counting":
            case "countingsort":
                if (criteria == SortCriteria.QUANTITY_ASC ||
                    criteria == SortCriteria.QUANTITY_DESC) {
                    lastAlgorithmUsed = "CountingSort (forced)";
                    countingSort(items, criteria);
                } else {
                    throw new IllegalArgumentException(
                        "CountingSort only works for quantity-based sorting");
                }
                break;

            case "insertion":
            case "insertionsort":
                lastAlgorithmUsed = "InsertionSort (forced)";
                insertionSort(items, criteria);
                break;

            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
